package kz.kalybayevv.VtbNews.web.dto;

import java.util.Map;

public class ToastrDto {
    public ToastrType type;
    public String message;
    public String errorId;
    public Map<String,Object> params;

    public ToastrDto() {

    }

    public ToastrDto(ToastrType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ToastrDto(ToastrType type, String message, Map<String, Object> params) {
        this.type = type;
        this.message = message;
        this.params = params;
    }

    public ToastrDto(ToastrType type, String message, String errorId) {
        this.type = type;
        this.message = message;
        this.errorId = errorId;
    }

    public ToastrDto(ToastrType error, String errorMessage, String errorId, Map<String, Object> params) {
        this.errorId = errorId;
        this.message = errorMessage;
        this.type = error;
        this.params = params;
    }
}
