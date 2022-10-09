package kz.kalybayevv.VtbNews.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;

    private String title;

    private LocalDate createdDate;

    private String author;

    private String description;

    private BigDecimal likes;

    private BigDecimal shares;

    private BigDecimal views;

    private Set<String> comments = new HashSet<>();

    private String link;

    private RoleDto roleDto;
}
