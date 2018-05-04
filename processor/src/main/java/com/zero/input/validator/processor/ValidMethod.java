package com.zero.input.validator.processor;

import com.squareup.javapoet.CodeBlock;

/**
 * Created by woshi on 2018/5/2.
 */

public class ValidMethod {
    final String name;

    ValidMethod(String name) {
        this.name = name;
    }

    CodeBlock validateBlock() {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("target.$N()", name);
        return builder.build();
    }
}
