package kz.kalybayevv.VtbNews.exceptions;

import kz.kalybayevv.VtbNews.constants.Errors;
import kz.kalybayevv.VtbNews.utils.IdGenerator;

import java.util.Map;


public class FLCException extends RuntimeException {
    public String errorId;
    private Map<String, Object> params;

    public FLCException(String message) {
        super(message);
    }

    public FLCException(String message, Map<String, Object> params) {
        super(message);
        this.errorId = IdGenerator.getBase36(6);
        this.params = params;
    }

    public static FLCException getErrorOccuredInstance() {
        return new FLCException(Errors.OCCURED);
    }

    public static FLCException getErrorOccuredTryAgainInstance() {
        return new FLCException(Errors.OCCURED_TRY_AGAIN);
    }

    public static void checkNotNull(Object object, String message) {
        if (object == null)
            throw new FLCException(message);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getErrorMessage() {
        return super.getMessage();
    }

    public String getErrorId() {
        return errorId;
    }

    @Override
    public String getMessage() {
        return errorId + "|" + super.getMessage();
    }
}
