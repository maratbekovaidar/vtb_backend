package kz.kalybayevv.VtbNews.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@UtilityClass
public class JwtTokenUtils {
    private static final String key = "UEBReVMyNFQoMg==KUlDJWM2NHtTQihndz5WNQ==dyg+TXksOjpLM2NcajV4cGhrNC8yKCQ3LnBKXThkLilvIXhbdUYoUEt6LXk=";
    private static final Integer expirationDay = 1;

    public static String generateJwt(String email, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder().setSubject(email).claim("authorities", authorities.stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(expirationDay)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))).compact();
    }

    public static Claims decodeJwt(String jwt) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))).parseClaimsJws(jwt).getBody();
    }
}
