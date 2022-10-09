package kz.kalybayevv.VtbNews.constants;

import java.util.List;

public class Constants {
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final Long COMMON_USER = 1L;

    public static final Long ACCOUNTANT = 2L;

    public static final Long BUSINESS = 3L;

    public static final String ROLE_PREFIX = "ROLE_";

    public static final String JAVA_EMAIL_SENDER = "david.lacey@yandex.ru";

    public static final int CONSTANT_ACT_DIGIT = 7;

    public static final String TEXT_ACT_SUBJECT = "Activate your account of VTB NEWS";

    public static final String CONTENT_ACT = "Your activation code from VTB NEWS account is \n ----------------- \n ";

    public static final List<String> ACCOUNTANT_NEWS = List.of("https://www.klerk.ru/news/page/%d/");

    public static final List<String> BUSINESS_NEWS = List.of("https://ru.euronews.com/api/hubpages/eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsaW1pdCI6MjAsInR5cGVbaW5dIjoiLG5vcm1hbCxldXJvbmV3c19uYmMiLCJ0eXBlW25pbl0iOiIiLCJsaXN0IjoiaWQsY2lkLHRpdGxlLHVybCxpbWFnZXMsdmlkZW8sdmlkZW9zLHRoZW1lcyx2ZXJ0aWNhbCxwcm9ncmFtLGNvdW50cnksY29udGluZW50LGxlYWRpbix2ZXJzaW9ucyx0ZWNobmljYWxUYWdzLGNhbm9uaWNhbCx0eXBlLHB1Ymxpc2hlZEF0LGNyZWF0ZWRBdCx1cGRhdGVkQXQsYWxsVmlld3MsYWxsVmlld3NNZXRhLGFkdmVydGlzaW5nLGFkdmVydGlzaW5nRGF0YSxkaXNwbGF5VHlwZSx0aXRsZUxpc3RpbmcxLHNjcmliYmxlTGl2ZUlkLHNjcmliYmxlTGl2ZVJpYmJvbixrZXl3b3JkcyxleHRlcm5hbFBhcnRuZXJzLHdpZGdldHMiLCJ0aGVtZSI6ImJ1c2luZXNzIn0.45KbOc1-meTqH9BUMOcjQYD7qnGT3n4IwvucsohIjf0?after=1577884145&extra=2&limit=2000");
}
