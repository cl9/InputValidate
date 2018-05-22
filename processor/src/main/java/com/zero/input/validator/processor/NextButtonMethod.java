package com.zero.input.validator.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Created by woshi on 2018/5/2.
 */

public class NextButtonMethod {
    private static final ClassName CONTEXT_COMPAT =
            ClassName.get("android.support.v4.content", "ContextCompat");
    final String name;
    final int enableBgColor;
    final int enableTextColor;
    final int disableBgColor;
    final int disableTextColor;

    NextButtonMethod(String name, int enableBgColor, int enableTextColor, int disableBgColor, int disableTextColor) {
        this.name = name;
        this.enableBgColor = enableBgColor;
        this.enableTextColor = enableTextColor;
        this.disableBgColor = disableBgColor;
        this.disableTextColor = disableTextColor;
    }

    CodeBlock buttonEnableBlock() {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("target.$N.setBackgroundColor($T.getColor(target,$L));\r\t", name, CONTEXT_COMPAT, enableBgColor)
                .add("target.$N.setTextColor($T.getColor(target,$L));\r\t", name, CONTEXT_COMPAT, enableTextColor)
                .add("target.$N.setEnabled(true);", name);
        return builder.build();
    }

    CodeBlock buttonDisableBlock() {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("target.$N.setBackgroundColor($T.getColor(target,$L));\r\t", name, CONTEXT_COMPAT, disableBgColor)
                .add("target.$N.setTextColor($T.getColor(target,$L));\r\t", name, CONTEXT_COMPAT, disableTextColor)
                .add("target.$N.setEnabled(false);", name);
        return builder.build();
    }

    /**
     * form button has two now.
     * 1. normal,validate all input is valid
     * 2. no-normal,button change enable when all input is not empty,
     * otherwise,button is disable
     *
     * @return
     */
    boolean isNormal() {
        return enableBgColor == -1 && enableTextColor == -1
                && disableBgColor == -1 && disableTextColor == -1;
    }
}
