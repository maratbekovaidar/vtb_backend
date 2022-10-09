package kz.kalybayevv.VtbNews.web.rest;

import com.codahale.metrics.annotation.Timed;
import kz.kalybayevv.VtbNews.constants.Errors;
import kz.kalybayevv.VtbNews.exceptions.FLCException;
import kz.kalybayevv.VtbNews.services.UserCommonService;
import kz.kalybayevv.VtbNews.utils.JwtTokenUtils;
import kz.kalybayevv.VtbNews.utils.StringUtils;
import kz.kalybayevv.VtbNews.web.dto.ResponseDto;
import kz.kalybayevv.VtbNews.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserCommonService userCommonService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registration process
     * <p/>
     * @param dto     User data which are mandatory to have a registration
     * @return ResponseDto (inside we have userId that's going to save image by userId);
     */
    @PostMapping("/register")
    @Timed
    public ResponseEntity<?> register(@RequestBody UserDto dto) {
        return ResponseEntity.ok(userCommonService.register(dto));
    }

    /**
     * Login process to get a jwt token and start to work with program
     * <p/>
     * @param email    User or Manager email
     * @param password User or Manager password
     * @return Jwt Token
     */
    @PostMapping("/login")
    @Timed
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        if (StringUtils.isAnyEmpty(email, password)) {
            throw new FLCException(Errors.NOT_VALID_FIELDS);
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        if (authentication.isAuthenticated()) {
            String token = JwtTokenUtils.generateJwt(authentication.getName(), authentication.getAuthorities());
            log.debug("User with email: {} - logged into the system", authentication.getName());
            return ResponseEntity.ok(ResponseDto.builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .data(token)
                    .build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
    }

    /**
     * Activate account with 2 params email and code (generated randomly)
     * <p/>
     * @return Response with HttpStatus
     */
    @PostMapping("/activate")
    @Timed
    public ResponseEntity<?> activateAcc(@RequestParam("email") String phoneNumber,
                                         @RequestParam(value = "code", required = false) String code) {
        return ResponseEntity.ok(userCommonService.activate(phoneNumber, code));
    }

    /**
     *  Reset password of user by sent code to the phone or email
     * @param oldPassword password that was used
     * @param newPassword new password that's gonna use
     * @param val email
     * @return Response
     */
    @PostMapping("/reset/password")
    @Timed
    public ResponseEntity<?> resetPassword(@RequestParam("oldPassword") String oldPassword,
                                           @RequestParam("newPassword") String newPassword,
                                           @RequestParam("email") String val) {
        return ResponseEntity.ok(userCommonService.resetPassword(oldPassword, newPassword, val));
    }
}
