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
    int enableBgColor();

    @ColorRes
    int enableTextColor();

    @ColorRes
    int disableBgColor();

    @ColorRes
    int disableTextColor();
}
