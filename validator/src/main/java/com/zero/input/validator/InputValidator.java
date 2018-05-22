package com.zero.input.validator;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by woshi on 2018/5/4.
 */

public class InputValidator {
    static final Map<Class<? extends Object>, Constructor<? extends Object>> VALIDATORS = new LinkedHashMap<>();
    private static final String TAG = "InputValidator";
    private static boolean debug = false;
    private static Object instance;
    private static Class<? extends Object> validatorClass;

    /**
     * Control whether debug logging is enabled.
     */
    public static void setDebug(boolean debug) {
        InputValidator.debug = debug;
    }

    @NonNull
    @UiThread
    public static void initInput(@NonNull Object target) {
        Class<? extends Object> targetClass = target.getClass();
        Constructor<? extends Object> constructor = findConstructorForClass(targetClass);

        try {
            instance = constructor.newInstance(target);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create validator instance.", cause);
        }
    }

    private static Constructor<? extends Object> findConstructorForClass(Class<? extends Object> cls) {
        Constructor<? extends Object> validatorCtor = VALIDATORS.get(cls);
        if (validatorCtor != null) {
            if (debug) Log.d(TAG, "HIT: Cached in validator map.");
            return validatorCtor;
        }
        String clsName = cls.getName();
        try {
            validatorClass = cls.getClassLoader().loadClass(clsName + "_InputValidator");
            validatorCtor = validatorClass.getConstructor(cls);
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            validatorCtor = findConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find validator constructor for " + clsName, e);
        }
        VALIDATORS.put(cls, validatorCtor);
        return validatorCtor;
    }

    @NonNull
    @UiThread
    public static void recycle() {
        try {
            Method method = validatorClass.getMethod("recycle");
            method.invoke(instance);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find validate method for " + validatorClass.getName(), e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
