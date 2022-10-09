package kz.kalybayevv.VtbNews.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import kz.kalybayevv.VtbNews.config.security.MCryptPasswordEncoder;
import kz.kalybayevv.VtbNews.utils.JwtTokenUtils;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
@Configuration
public class UaaWebSecurityConfiguration extends OncePerRequestFilter {
    private final String header = HttpHeaders.AUTHORIZATION;
    private final String prefix = "Bearer ";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MCryptPasswordEncoder();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            if (checkJwtToken(request)) {
                Claims claims = validateToken(request);
                if (claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(SC_FORBIDDEN);
            response.sendError(SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwt = request.getHeader(header).replace(prefix, "");
        return JwtTokenUtils.decodeJwt(jwt);
    }

    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked") List<String> authorities = (List) claims.get("authorities");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(),
                null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }


    private boolean checkJwtToken(HttpServletRequest request) {
        String jwt = request.getHeader(header);
        return !isNullOrEmpty(jwt) && jwt.startsWith(prefix);
    }
}
