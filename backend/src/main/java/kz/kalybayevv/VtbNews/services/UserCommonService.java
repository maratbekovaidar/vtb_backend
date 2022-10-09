package kz.kalybayevv.VtbNews.services;

import kz.kalybayevv.VtbNews.entity.User;
import kz.kalybayevv.VtbNews.web.dto.ResponseDto;
import kz.kalybayevv.VtbNews.web.dto.UserDto;

import java.util.Optional;
import java.util.Set;

public interface UserCommonService {
    ResponseDto<?> register(UserDto dto);

    Optional<User> findByEmail(String email);

    Set<String> getUserAuthorities(Long id);

    ResponseDto<?> activate(String email, String code);

    ResponseDto<?> getUserInformation(Long id);

    ResponseDto<?> updateUser(UserDto dto);

    ResponseDto<?> resetPassword(String oldPassword, String newPassword, String email);

    ResponseDto<?> deleteUser(Long id);
}
