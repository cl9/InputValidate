package com.zero.input.validator.annotations;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woshi on 2018/4/24.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface TelPhone {
    String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}";

    @StringRes
    int errMsg() default -1;
}
