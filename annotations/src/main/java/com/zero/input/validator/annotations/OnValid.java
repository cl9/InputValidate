package com.zero.input.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by woshi on 2018/4/20.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnValid {

}
