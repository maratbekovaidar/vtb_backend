package kz.kalybayevv.VtbNews.web.rest;

import com.codahale.metrics.annotation.Timed;
import kz.kalybayevv.VtbNews.services.UserCommonService;
import kz.kalybayevv.VtbNews.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {
    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final UserCommonService userCommonService;

    /**
     * Get User Information by id or by security context holder
     * @param id UserId
     * @return User Information
     */
    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<?> getUserInformation(@PathVariable(value = "id", required = false) Long id) {
        return ResponseEntity.ok(userCommonService.getUserInformation(id));
    }

    /**
     * Delete Account by user id or by security context holder
     * @param id User id
     * @return Response HttpStatus
     */
    @PostMapping("/delete/{id}")
    @Timed
    public ResponseEntity<?> deleteAccount(@PathVariable(value = "id", required = false) Long id) {
        return ResponseEntity.ok(userCommonService.deleteUser(id));
    }

    /**
     * Update Account by user id or by security context holder
     * @param dto User
     * @return Response changed data
     */
    @PutMapping("/update")
    @Timed
    public ResponseEntity<?> updateAccount(@RequestBody UserDto dto) {
        log.debug("User {} making an update of account", dto.getId());
        return ResponseEntity.ok(userCommonService.updateUser(dto));
    }
}
