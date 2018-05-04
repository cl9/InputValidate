package com.zero.input.validator.processor;

import android.support.annotation.StringRes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.zero.input.validator.annotations.ValidatorUtils;

/**
 * Created by woshi on 2018/5/2.
 */

public class RegxInput {
    private static final ClassName TOAST = ClassName.get("android.widget", "Toast");
    final String name;
    final String pattern;
    final int errMsg;

    RegxInput(String name, String pattern, @StringRes int errMsg) {
        this.name = name;
        this.pattern = pattern;
        this.errMsg = errMsg;
    }

    CodeBlock validateBlock() {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("if(!$T.isValid(target.$N.getText().toString().trim(),$S))", ValidatorUtils.class, name, pattern);
        builder.add("{");
        builder.add("\n");
        if (errMsg == -1) {
            // if not set errMsg，use TextView.getHint()
            builder.add("$T.makeText($N,$N.$N.getHint().toString().trim(),Toast.LENGTH_SHORT).show();", TOAST, "target", "target", name);
        } else {
            builder.add("$T.makeText($N,$N.getResources().getString($L),Toast.LENGTH_SHORT).show();", TOAST, "target", "target", errMsg);
        }
        builder.add("\n");
        builder.add("requestFocus(target.$N);", name);
        builder.add("\n");
        builder.add("return;");
        builder.add("\r\t");
        return builder.add("}").build();
    }
}
