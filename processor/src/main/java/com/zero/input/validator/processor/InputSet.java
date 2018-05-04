package com.zero.input.validator.processor;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zero.input.validator.annotations.ValidatorUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.VOID;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by woshi on 2018/5/2.
 */

public class InputSet {
    private static final ClassName UI_THREAD =
            ClassName.get("android.support.annotation", "UiThread");
    private static final ClassName TEXT_VIEW =
            ClassName.get("android.widget", "TextView");
    private static final ClassName TEXT_WATCHER =
            ClassName.get("android.text", "TextWatcher");
    private static final ClassName EDITABLE =
            ClassName.get("android.text", "Editable");
    private final TypeName targetTypeName;
    private final ClassName bindingClassName;
    private final boolean isFinal;
    private final ImmutableList<RegxInput> regxInputs;
    private final ImmutableList<NotEmptyInput> notEmptyInputs;
    private final ImmutableList<IDCardInput> idCardInputs;
    private final ValidMethod validMethod;
    private final ImmutableList<String> names;
    private final NextButtonMethod nextButtonMethod;

    private InputSet(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal,
                     ImmutableList<RegxInput> regxInputs, ImmutableList<NotEmptyInput> notEmptyInputs,
                     ImmutableList<IDCardInput> idCardInputs, ImmutableList<String> names, ValidMethod validMethod, NextButtonMethod nextButtonMethod) {
        this.isFinal = isFinal;
        this.targetTypeName = targetTypeName;
        this.bindingClassName = bindingClassName;
        this.regxInputs = regxInputs;
        this.notEmptyInputs = notEmptyInputs;
        this.idCardInputs = idCardInputs;
        this.names = names;
        this.validMethod = validMethod;
        this.nextButtonMethod = nextButtonMethod;
    }

    JavaFile brewJava() {
        return JavaFile.builder(bindingClassName.packageName(), createType())
                .build();
    }

    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(bindingClassName.simpleName())
                .addModifiers(PUBLIC);
        if (isFinal) {
            result.addModifiers(FINAL);
        }

        result.addField(targetTypeName, "target", PRIVATE);

        if (nextButtonMethod != null) {
            createNameListField(result);
            result.addMethod(createIsAllMethod());
            result.addMethod(createWatcherEmptyMethod());
            result.addMethod(createEnableBtnMethod());
            result.addMethod(createDisableBtnMethod());
        }
        result.addMethod(createConstructorMethod());

        result.addMethod(createRequestFocusMethod());

        result.addMethod(createValidateMethod());

        return result.build();
    }

    private MethodSpec createDisableBtnMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("buttonDisable")
                .addModifiers(PRIVATE)
                .addAnnotation(UI_THREAD)
                .returns(VOID);
        builder.addStatement("$L", nextButtonMethod.buttonDisableBlock());
        return builder.build();
    }

    private MethodSpec createEnableBtnMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("buttonEnable")
                .addModifiers(PRIVATE)
                .addAnnotation(UI_THREAD)
                .returns(VOID);
        builder.addStatement("$L", nextButtonMethod.buttonEnableBlock());
        return builder.build();
    }

    private MethodSpec createIsAllMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("isAllNotEmpty")
                .addModifiers(PRIVATE)
                .addAnnotation(UI_THREAD)
                .returns(BOOLEAN);
        builder.addStatement("boolean isAllNotEmpty = true;");
        builder.beginControlFlow("for($T tv : tvList)", TEXT_VIEW);
        builder.beginControlFlow("if($T.isEmpty(tv.getText().toString().trim()))", ValidatorUtils.class);
        builder.addStatement("isAllNotEmpty = false;");
        builder.endControlFlow();
        builder.endControlFlow();
        builder.addStatement("return isAllNotEmpty");
        return builder.build();
    }

    private MethodSpec createWatcherEmptyMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("watcherEmpty")
                .addModifiers(PUBLIC)
                .addAnnotation(UI_THREAD);
        builder.beginControlFlow("for($T tv : tvList)", TEXT_VIEW);

        builder.addStatement("tv.addTextChangedListener($L)", createAnonymousClass(builder));

        builder.endControlFlow();
        return builder.build();
    }

    private TypeSpec createAnonymousClass(MethodSpec.Builder builder) {
        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(TEXT_WATCHER)
                .addMethod(MethodSpec.methodBuilder("beforeTextChanged")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(CharSequence.class, "s")
                        .addParameter(INT, "start")
                        .addParameter(INT, "count")
                        .addParameter(INT, "after")
                        .returns(VOID)
                        .build())
                .addMethod(MethodSpec.methodBuilder("onTextChanged")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(CharSequence.class, "s")
                        .addParameter(INT, "start")
                        .addParameter(INT, "before")
                        .addParameter(INT, "count")
                        .returns(VOID)
                        .build())
                .addMethod(MethodSpec.methodBuilder("afterTextChanged")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(EDITABLE, "s")
                        .beginControlFlow("if(isAllNotEmpty())")
                        .addStatement("buttonEnable()")
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("buttonDisable()")
                        .endControlFlow()
                        .returns(VOID)
                        .build())
                .build();
    }

    private void createNameListField(TypeSpec.Builder result) {
        ClassName list = ClassName.get("java.util", "List");
        TypeName nameList = ParameterizedTypeName.get(list, TEXT_VIEW);
        result.addField(nameList, "tvList", PRIVATE);
    }

    private MethodSpec createRequestFocusMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("requestFocus")
                .addModifiers(PRIVATE)
                .addAnnotation(UI_THREAD)
                .addParameter(TEXT_VIEW, "tv");
        builder.addStatement("tv.setFocusable(true)");
        builder.addStatement("tv.setFocusableInTouchMode(true)");
        builder.addStatement("tv.requestFocus()");
        return builder.build();
    }

    private MethodSpec createConstructorMethod() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(PUBLIC)
                .addParameter(targetTypeName, "target");
        builder.addStatement("this.$N = $N", "target", "target");
        if (nextButtonMethod != null) {
            initialTvList(builder);
            builder.addStatement("buttonDisable()");
        }
        return builder.build();
    }

    /**
     * initial list TextView field tvList，and add data
     *
     * @param builder
     */
    private void initialTvList(MethodSpec.Builder builder) {
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        builder.addStatement("tvList = new $T<>()", arrayList);
        for (String name : names) {
            builder.addStatement("tvList.add(target.$N)", name);
        }
        builder.addStatement("watcherEmpty()");
    }

    /**
     * create validate method
     *
     * @return
     */
    private MethodSpec createValidateMethod() {
        MethodSpec.Builder validate = MethodSpec.methodBuilder("validate")
                .addAnnotation(UI_THREAD)
                .addModifiers(PUBLIC)
                .returns(VOID);

        for (RegxInput regxInput : regxInputs) {
            validate.addStatement("$L", regxInput.validateBlock());
        }

        for (NotEmptyInput notEmptyInput : notEmptyInputs) {
            validate.addStatement("$L", notEmptyInput.validateBlock());
        }

        for (IDCardInput idCardInput : idCardInputs) {
            validate.addStatement("$L", idCardInput.validateBlock());
        }

        if (validMethod != null) {
            validate.addStatement("$L", validMethod.validateBlock());
        }
        return validate.build();
    }

    static Builder newBuilder(TypeElement enclosingElement) {
        TypeMirror typeMirror = enclosingElement.asType();

        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }

        String packageName = getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');
        ClassName bindingClassName = ClassName.get(packageName, className + "_InputValidator");

        boolean isFinal = enclosingElement.getModifiers().contains(FINAL);
        return new Builder(targetType, bindingClassName, isFinal);
    }

    static final class Builder {
        private final TypeName targetTypeName;
        private final ClassName bindingClassName;
        private final boolean isFinal;
        private final ImmutableList.Builder<RegxInput> regxInputs =
                ImmutableList.builder();
        private final ImmutableList.Builder<NotEmptyInput> notEmptyInputs =
                ImmutableList.builder();
        private final ImmutableList.Builder<IDCardInput> idCardInputs =
                ImmutableList.builder();
        private final ImmutableList.Builder<String> names =
                ImmutableList.builder();
        private ValidMethod validMethod;
        private NextButtonMethod nextButtonMethod;
        private InputSet parentInput;

        private Builder(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal) {
            this.targetTypeName = targetTypeName;
            this.bindingClassName = bindingClassName;
            this.isFinal = isFinal;
        }

        void addRegx(RegxInput input) {
            regxInputs.add(input);
            addName(input.name);
        }

        void addNotEmpty(NotEmptyInput input) {
            notEmptyInputs.add(input);
            addName(input.name);
        }

        void addIdCard(IDCardInput input) {
            idCardInputs.add(input);
            addName(input.name);
        }

        void addName(String name) {
            names.add(name);
        }

        void setOnValidMethod(ValidMethod validMethod) {
            this.validMethod = validMethod;
        }

        void setOnButtonEnableMethod(NextButtonMethod nextButtonMethod) {
            this.nextButtonMethod = nextButtonMethod;
        }

        public void setParent(InputSet parentInput) {
            this.parentInput = parentInput;
        }

        public InputSet build() {
            return new InputSet(targetTypeName, bindingClassName, isFinal,
                    regxInputs.build(), notEmptyInputs.build(), idCardInputs.build(), names.build(),
                    validMethod, nextButtonMethod);
        }
    }
}
