package kz.kalybayevv.VtbNews.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;

    private String name;

    private String nameKk;

    private String nameRu;

    private String nameEn;
}
