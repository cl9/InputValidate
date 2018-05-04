package com.zero.input.validator.processor;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.zero.input.validator.annotations.Date;
import com.zero.input.validator.annotations.Email;
import com.zero.input.validator.annotations.IDCard;
import com.zero.input.validator.annotations.Mobile;
import com.zero.input.validator.annotations.NotEmpty;
import com.zero.input.validator.annotations.NextButton;
import com.zero.input.validator.annotations.OnValid;
import com.zero.input.validator.annotations.PassWord;
import com.zero.input.validator.annotations.TelPhone;
import com.zero.input.validator.annotations.ValidatorUtils;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.zero.input.validator.annotations.Date", "com.zero.input.validator.annotations.Email",
        "com.zero.input.validator.annotations.IDCard", "com.zero.input.validator.annotations.Mobile",
        "com.zero.input.validator.annotations.PassWord", "com.zero.input.validator.annotations.TelPhone",
        "com.zero.input.validator.annotations.NotEmpty", "com.zero.input.validator.annotations.OnValid",
        "com.zero.input.validator.annotations.NextButton"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ValidatorProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, InputSet> bindingMap = findAnnotation(roundEnv);

        for (Map.Entry<TypeElement, InputSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            InputSet binding = entry.getValue();

            JavaFile javaFile = binding.brewJava();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, String.format("Unable to write binding for type %s: %s", typeElement, e.getMessage()));
            }
        }
        return false;
    }

    /**
     * process each annotation
     *
     * @param env
     */
    private Map<TypeElement, InputSet> findAnnotation(RoundEnvironment env) {
        Map<TypeElement, InputSet.Builder> builderMap = new LinkedHashMap<>();
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

        for (Element element : env.getElementsAnnotatedWith(PassWord.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parsePassWord(builderMap, element, erasedTargetNames);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.NOTE, e.getMessage());
            }
        }

        for (Element element : env.getElementsAnnotatedWith(NotEmpty.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseNotEmpty(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(OnValid.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseOnValidMethod(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(NextButton.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseNextButtonMethod(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(Date.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseDate(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(Email.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseEmail(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(Mobile.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseMobile(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(TelPhone.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseTelPhone(builderMap, element, erasedTargetNames);
        }

        for (Element element : env.getElementsAnnotatedWith(IDCard.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseIDCard(builderMap, element, erasedTargetNames);
        }

        Deque<Map.Entry<TypeElement, InputSet.Builder>> entries =
                new ArrayDeque<>(builderMap.entrySet());
        Map<TypeElement, InputSet> inputMap = new LinkedHashMap<>();
        while (!entries.isEmpty()) {
            Map.Entry<TypeElement, InputSet.Builder> entry = entries.removeFirst();

            TypeElement type = entry.getKey();
            InputSet.Builder builder = entry.getValue();

            TypeElement parentType = findParentType(type, erasedTargetNames);
            if (parentType == null) {
                inputMap.put(type, builder.build());
            } else {
                InputSet parentInput = inputMap.get(parentType);
                if (parentInput != null) {
                    builder.setParent(parentInput);
                    inputMap.put(type, builder.build());
                } else {
                    // Has a superclass  but we haven't built it yet. Re-enqueue for later.
                    entries.addLast(entry);
                }
            }
        }
        return inputMap;
    }

    private void parseNextButtonMethod(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @NotEmpty EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        NextButton nextButton = element.getAnnotation(NextButton.class);

        builder.setOnButtonEnableMethod(new NextButtonMethod(name,nextButton.enableBgColor(),
                nextButton.enableTextColor(),nextButton.disableBgColor(),nextButton.disableTextColor()));
    }

    private TypeElement findParentType(TypeElement typeElement, Set<TypeElement> parents) {
        TypeMirror type;
        while (true) {
            type = typeElement.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) type).asElement();
            if (parents.contains(typeElement)) {
                return typeElement;
            }
        }
    }

    /**
     * process PassWord annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parsePassWord(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) throws Exception {
        // enclosingElement is class enclosingName for @PassWord ,for example:com.zero.input.apt.MainActivity
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @PassWord EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        PassWord passWord = element.getAnnotation(PassWord.class);

        String pattern;
        if (passWord.scheme() == PassWord.Scheme.CUSTOM) {
            if (ValidatorUtils.isEmpty(passWord.customRegx())) {
                throw new Exception("if use Scheme.Custom，you must specify custom regx with customRegx()");
            } else {
                pattern = passWord.customRegx();
            }
        } else {
            pattern = PassWord.SCHEME_PATTERNS.get(passWord.scheme());
        }

        builder.addRegx(new RegxInput(name, pattern, passWord.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process NotEmpty annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseNotEmpty(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @NotEmpty EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        NotEmpty notEmpty = element.getAnnotation(NotEmpty.class);

        builder.addNotEmpty(new NotEmptyInput(name, notEmpty.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process @Date annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseDate(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @Date EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        Date date = element.getAnnotation(Date.class);

        String pattern = Date.REGEX_DATE;

        builder.addRegx(new RegxInput(name, pattern, date.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process @Email annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseEmail(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @Date EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        Email email = element.getAnnotation(Email.class);

        String pattern = Email.REGEX_EMAIL;

        builder.addRegx(new RegxInput(name, pattern, email.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process @Mobile annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseMobile(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @Date EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        Mobile mobile = element.getAnnotation(Mobile.class);

        String pattern = Mobile.REGX_MOBILE;

        builder.addRegx(new RegxInput(name, pattern, mobile.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process @TelPhone annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseTelPhone(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @Date EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        TelPhone telPhone = element.getAnnotation(TelPhone.class);

        String pattern = TelPhone.REGEX_TEL;

        builder.addRegx(new RegxInput(name, pattern, telPhone.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process @IDCard annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseIDCard(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @NotEmpty EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();
        IDCard notEmpty = element.getAnnotation(IDCard.class);

        builder.addIdCard(new IDCardInput(name, notEmpty.errMsg()));

        erasedTargetNames.add(enclosingElement);
    }

    /**
     * process OnValid annotation
     *
     * @param builderMap
     * @param element
     * @param erasedTargetNames
     */
    private void parseOnValidMethod(Map<TypeElement, InputSet.Builder> builderMap, Element element, Set<TypeElement> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = getOrCreateInputBuilder(builderMap, enclosingElement);
        }
        // a @NotEmpty EditText,for example MainActivity's etPwd
        String name = element.getSimpleName().toString();

        builder.setOnValidMethod(new ValidMethod(name));
    }

    private InputSet.Builder getOrCreateInputBuilder(
            Map<TypeElement, InputSet.Builder> builderMap, TypeElement enclosingElement) {
        InputSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = InputSet.newBuilder(enclosingElement);
            builderMap.put(enclosingElement, builder);
        }
        return builder;
    }
}
