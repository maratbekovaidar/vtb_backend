package kz.kalybayevv.VtbNews.config.security;

import kz.kalybayevv.VtbNews.constants.Errors;
import kz.kalybayevv.VtbNews.entity.User;
import kz.kalybayevv.VtbNews.exceptions.FLCException;
import kz.kalybayevv.VtbNews.services.UserCommonService;
import kz.kalybayevv.VtbNews.utils.ObjectMapper;
import kz.kalybayevv.VtbNews.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserCommonService userCommonService;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest servletRequest;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authenticating user with email {}", email);

        Optional<User> userFromDb = userCommonService.findByEmail(email);
        if (userFromDb.isEmpty()) {
            throw new FLCException(Errors.NO_USER);
        }
        User user = userFromDb.get();
        if (BooleanUtils.isFalse(user.getActivated())) {
            throw new FLCException(Errors.ACCOUNT_NOT_ACTIVATED);
        }
        if (BooleanUtils.isTrue(user.getDeleted())) {
            throw new FLCException(Errors.ERR_ACCESS_DENIED);
        }
        String password = servletRequest.getParameter("password");

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new FLCException(Errors.INCORRECT_PASSWORD);
        }

        List<GrantedAuthority> grantedAuthorities = userCommonService.getUserAuthorities(user.getId())
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.debug("Granted authorities, {}", grantedAuthorities);

        return new org.springframework.security.core.userdetails.User(email, user.getPassword(), grantedAuthorities);
    }
}
