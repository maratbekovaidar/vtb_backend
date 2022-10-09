package kz.kalybayevv.VtbNews.constants;

public interface Errors {
    String OCCURED = "error.occured";
    String OCCURED_TRY_AGAIN = "error.occured-try-again";
    String FORBIDDEN_ACTION = "error.forbidden-action";
    String INCORRECT_DICTIONARY = "error.incorrect-dictionary";
    String NO_USER_SUBDIVISION = "error.no-user-subdivision";
    String NO_USER = "error.no-user";
    String ACCOUNT_NOT_ACTIVATED = "error.account-not-activated";
    String USER_EXISTS = "error.user-already-exists";
    String NOT_VALID_FIELDS = "error.not-valid-fields";
    String UNAUTHORIZED = "error.user-unauthorized";
    String INCORRECT_PASSWORD = "error.incorrect-password";
    String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    String ERR_ACCESS_DENIED = "error.accessDenied";
    String ERR_VALIDATION = "error.validation";
    String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
    String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
    String ERR_CODE_HAVE_NOT_SENT = "error.code-have-not-sent";
    String ERR_NOT_REQUIRED_TYPE_OF_FILE = "error.not-required-type-of-file";

    String ERR_NOT_MATCHES = "error.not-matches";

    static String error(String key) {
        return "error." + key;
    }
}
