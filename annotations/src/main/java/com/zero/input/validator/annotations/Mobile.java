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
public @interface Mobile {
    String REGX_MOBILE = "^((13[0-9])|(14[0-9])|(15[0-9])|(16[6])|(17[0-9])|(18[0-9])|(19[8,9]))\\d{8}$";

    @StringRes
    int errMsg() default -1;
}
