package kz.kalybayevv.VtbNews.utils;

import kz.kalybayevv.VtbNews.entity.Role;
import kz.kalybayevv.VtbNews.entity.User;
import kz.kalybayevv.VtbNews.web.dto.RoleDto;
import kz.kalybayevv.VtbNews.web.dto.UserDto;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class ObjectMapper {
    public static UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .activated(user.getActivated())
                .email(user.getEmail())
                .deleted(user.getDeleted())
                .roles(user.getRoles().stream()
                        .map(ObjectMapper::convertToRoleDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static RoleDto convertToRoleDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .nameKk(role.getNameKk())
                .nameRu(role.getNameRu())
                .nameEn(role.getNameEn())
                .build();
    }
}
