package kz.kalybayevv.VtbNews.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    @Builder.Default
    private int httpStatus = 200;
    private boolean success;
    private String errMessage;
    private T data;
}
