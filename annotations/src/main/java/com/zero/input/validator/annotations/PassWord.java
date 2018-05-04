package com.zero.input.validator.annotations;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface PassWord {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String customRegx() default "";

    Scheme scheme() default Scheme.ANY;

    @StringRes
    int errMsg() default -1;

    enum Scheme {
        ANY,
        LETTER,
        LETTER_MIXED_CASE,
        NUMERIC,
        LETTER_NUMERIC,
        LETTER_NUMERIC_MIXED_CASE,
        LETTER_NUMERIC_SYMBOLS,
        LETTER_NUMERIC_MIXED_CASE_SYMBOLS,
        CUSTOM
    }

    final Map<Scheme, String> SCHEME_PATTERNS =
            new HashMap<Scheme, String>() {{
                put(Scheme.ANY, ".+");
                put(Scheme.LETTER, "\\w+");
                put(Scheme.LETTER_MIXED_CASE, "(?=.*[a-z])(?=.*[A-Z]).+");
                put(Scheme.NUMERIC, "\\d+");
                put(Scheme.LETTER_NUMERIC, "(?=.*[a-zA-Z])(?=.*[\\d]).+");
                put(Scheme.LETTER_NUMERIC_MIXED_CASE,
                        "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).+");
                put(Scheme.LETTER_NUMERIC_SYMBOLS,
                        "(?=.*[a-zA-Z])(?=.*[\\d])(?=.*([^\\w])).+");
                put(Scheme.LETTER_NUMERIC_MIXED_CASE_SYMBOLS,
                        "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*([^\\w])).+");
            }};
}
