package kz.kalybayevv.VtbNews.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String firstname;

    private String lastname;

    private Boolean activated;

    private String email;

    private String password;

    private Boolean deleted;

    private Set<RoleDto> roles;
}
