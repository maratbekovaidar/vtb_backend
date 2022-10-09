package kz.kalybayevv.VtbNews.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class StringUtils {
    public static String TAB = "\t";

    public static String DEFAULT_DELIM = ", ";

    public static String POINT_DELIM = ".";

    public static String PIPE_DELIM = "|";

    public static String DEFAULT_EMPTY = "-";

    public static String EMPTY = "";

    public static String nullToEmpty(String value) {
        return isEmpty(value) ? "" : value;
    }

    public static String nullToEmpty(String value, String delim) {
        return isEmpty(value) ? (isEmpty(delim) ? "" : delim) : value;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnyEmpty(String... values) {
        return ArrayUtils.isNotEmpty(values) && Stream.of(values).anyMatch(StringUtils::isEmpty);
    }

    public static boolean isAllEmpty(String... values) {
        return ArrayUtils.isNotEmpty(values) && Stream.of(values).allMatch(StringUtils::isEmpty);
    }

    @SneakyThrows
    public static String getBasicAuthorizationToken(String user, String password) {
        return Base64.encodeBase64String((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static boolean checkBasicAuthorizationToken(String user, String password, String token) {
        String[] split = token.split(" ");
        if (split.length > 1) {//отрезаем префикс "Basic "
            token = split[1];
        }
        return token.equals(getBasicAuthorizationToken(user, password));
    }

    public static List<String> split(String value) {
        if (isEmpty(value)) {
            return Collections.emptyList();
        }
        return split(value, DEFAULT_DELIM);
    }

    public static List<String> split(String value, String delim) {
        List<String> list = new ArrayList<>();
        if (isNotEmpty(value)) {
            for (String s : value.split(delim)) {
                list.add(s.trim());
            }
        }
        return list;
    }

    /**
     * разбить текст на блоки фиксированной длины
     *
     * @param text      текст
     * @param blockSize размер блока
     * @return блоки
     */
    public static List<String> splitIntoBlocks(String text, int blockSize) {
        if (blockSize < 1) throw new IllegalArgumentException("Incorrect block size");
        if (text == null) return null;
        List<String> list = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            list.add(text.substring(i, Math.min(i += blockSize, text.length())));
        }
        return list;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * получение полного ФИО
     *
     * @param lastname   фамилия
     * @param firstname  имя
     * @param patronymic отчество
     * @return ФИО
     */
    public static String getFullName(String lastname, String firstname, String patronymic) {
        StringBuilder sb = new StringBuilder();
        if (isNotEmpty(lastname)) {
            sb.append(lastname);
        }
        if (isNotEmpty(firstname)) {
            sb.append(" ");
            sb.append(firstname);
        }
        if (isNotEmpty(patronymic)) {
            sb.append(" ");
            sb.append(patronymic);
        }
        return sb.toString();
    }

    public static String getShortName(String lastname, String firstname, String patronymic) {
        StringBuilder sb = new StringBuilder();
        if (lastname != null) {
            sb.append(lastname);
        }
        if (firstname != null && firstname.length() > 0) {
            sb.append(" ").append(firstname.substring(0, 1).toUpperCase()).append(".");
        }
        if (patronymic != null && patronymic.length() > 0) {
            sb.append(" ").append(patronymic.substring(0, 1).toUpperCase()).append(".");
        }
        return sb.toString();
    }

    public static String[] splitByEndOfLine(String value) {
        return splitByRegex(value, "\\r?\\n");
    }

    public static String[] splitByTab(String value) {
        return splitByRegex(value, "\t");
    }

    public static String[] splitByRegex(String value, String regex) {
        return value == null ? null : value.split(regex, -1);
    }

    public static String[] splitBySubstringLength(String text, int subStringLength) {
        return text.split("(?<=\\G.{" + subStringLength + "})");
    }

    public static List<Map<String, String>> dataToMap(String value) {
        return dataToMap(value, TAB);
    }

    public static List<Map<String, String>> dataToMap(String value, String regex) {
        String lines[] = StringUtils.splitByEndOfLine(value);

        String[] headers = StringUtils.splitByTab(lines[0]);

        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] split = StringUtils.splitByRegex(lines[i], regex);
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                map.put(headers[j], split[j]);
            }
            list.add(map);
        }
        return list;
    }

    @SneakyThrows
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String getString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static List<String> getFilledLines(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            try (InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8)) {
                try (BufferedReader br = new BufferedReader(isr)) {
                    List<String> list = new ArrayList<>();
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (StringUtils.isNotEmpty(line)) {
                            list.add(line.trim());
                        }
                    }

                    return list;
                }
            }
        }
    }

    public static String emptyToDash(String value) {
        return emptyToDefault(value, "-");
    }

    public static String emptyToDash(Object obj) {
        return emptyToDash(obj == null ? null : obj.toString());
    }

    public static String emptyToDefault(String value, String defaultValue) {
        return isNotEmpty(value) ? value : defaultValue;
    }

    public static String emptyToDefault(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

    //склеивание строк с разделителем
    public static String merge(String[] arr) {
        return merge(Arrays.asList(arr));
    }

    //склеивание строк с разделителем
    public static String mergeStrings(String... arr) {
        return merge(Arrays.asList(arr));
    }

    //склеивание строк с разделителем
    public static String merge(String delim, String... arr) {
        return merge(Arrays.asList(arr), delim);
    }

    //склеивание строк с разделителем
    public static String merge(String[] arr, String delim) {
        return merge(Arrays.asList(arr), delim);
    }


    //склеивание строк с разделителем
    public static String merge(List<String> list) {
        return merge(list, DEFAULT_DELIM);
    }

    //склеивание строк с разделителем
    public static String merge(List<String> list, String delim) {
        StringBuilder sb = new StringBuilder();
        boolean can = false;
        for (String text : list) {
            if (isEmpty(text)) continue;
            if (can) sb.append(delim);
            sb.append(text);
            can = true;
        }
        return sb.toString();
    }

    public static String getJoinedLongs(Set<Long> longs) {
        return longs.stream().map(String::valueOf).collect(Collectors.joining(DEFAULT_DELIM));
    }

    public static String getJoinedString(List<String> strings) {
        return strings.stream().map(s -> "'" + s + "'").collect(Collectors.joining(", "));
    }

    public static String mergeWithoutDelimeter(String[] arr) {
        return mergeWithoutDelimeter(Arrays.asList(arr));
    }

    public static String mergeWithoutDelimeter(List<String> list) {
        StringBuilder tmp = new StringBuilder();
        for (String s : list) {
            tmp.append(s);
        }
        return tmp.toString();
    }

    public static String lowerCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    public static String upperCase(final String str) {
        if (isEmpty(str)) {
            return null;
        }
        return str.toUpperCase();
    }

    public static String capitalize(final String str) {
        if (isEmpty(str)) {
            return null;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String notEmptyOrThrow(String value, RuntimeException e) {
        if (isEmpty(value)) {
            throw e;
        }
        return value;
    }

    public static String notEmptyOrThrow(String value, Exception e) throws Exception {
        if (isEmpty(value)) {
            throw e;
        }
        return value;
    }

    public static boolean isNumeric(String text) {
        return org.apache.commons.lang3.StringUtils.isNumeric(text);
    }

    @SneakyThrows
    public static String getFileName(HttpServletRequest request, String fileName, String encoded) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.toLowerCase().contains("firefox")) { // firefox
            // удаление лишних пробелом при кодировании названия файлов
            return fileName.equals(encoded) ? encoded : encoded.replace(" ", "");
        }
        return encoded;
    }

    @SneakyThrows
    public static String encodeUTF8(String text) {
        if (isNotEmpty(text)) {
            return UriUtils.encode(text, StandardCharsets.UTF_8);
        }
        return text;
    }

    public static String prepareQueryString(String text) {
        return Arrays.stream(text.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", " ").trim().replaceAll("\\s{2,}", " ").split("[\\s]+")).map(x -> "*" + x + "*").collect(Collectors.joining(" "));
    }

    public static boolean equals(String str1, String str2) {
        return org.apache.commons.lang3.StringUtils.equals(str1, str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return org.apache.commons.lang3.StringUtils.equalsIgnoreCase(str1, str2);
    }

    public static String toUtf8String(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @desc oracle nvl analog
     */
    public static String nvl(String val1, String val2) {
        return isEmpty(val1) ? val2 : val1;
    }

    public static String substring(String string, int start, int end) {
        return org.apache.commons.lang3.StringUtils.substring(string, start, end);
    }

    public static String replaceSymbols(String text) {
        if (isNotEmpty(text)) {
            text = text.replace("[", "").replace("]", "");
            return text;
        }
        return "";
    }

    public static String replaceBr(String text) {
        return nullToEmpty(text).replace("<br/>", "\n");
    }

    public static String replaceTriangularBrackets(String text) {
        return nullToEmpty(text).replace("<", "&lt;").replace(">", "&gt;");
    }

    public static boolean isHtml(String text) {
        return isNotEmpty(text) && text.contains("/html");
    }

    public static boolean anyMatch(List<String> arr, String... values) {
        for (String val : arr) {
            if (Arrays.asList(values).contains(val)) {
                return true;
            }
        }
        return false;
    }
}
