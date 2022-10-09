package kz.kalybayevv.VtbNews.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

@UtilityClass
public class LogUtils {
    public static void info(Logger log, String message) {
        log.info(prefix() + message);
    }

    public static void info(Logger log, String format, Object... objects) {
        log.info(prefix() + format, objects);
    }

    private static String prefix() {
        return SecurityUtils.getCurrentUserLogin() + " (" + SecurityUtils.getRequestRemoteAddr() + ") ";
    }

    public static void error(Logger log, Throwable t, String message, Object... objects) {
        log.error(prefix() + MessageFormatter.format(message, objects).getMessage(), t);
    }
}
