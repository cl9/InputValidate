package com.zero.input.validator.annotations;

import android.support.annotation.ColorRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woshi on 2018/5/3.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface NextButton {

    @ColorRes
    int enableBgColor() default -1;

    @ColorRes
    int enableTextColor() default -1;

    @ColorRes
    int disableBgColor() default -1;

    @ColorRes
    int disableTextColor() default -1;
}
