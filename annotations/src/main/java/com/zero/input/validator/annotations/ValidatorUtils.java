package com.zero.input.validator.annotations;

public class ValidatorUtils {

    public static boolean isValid(String result, String pattern) {
        if (isEmpty(result)) {
            return false;
        }
        return result.matches(pattern);
    }

    public static boolean isIDCardValid(String result) {
        return isIDCard15(result) || isIDCard18(result);
    }

    private static boolean isIDCard15(String result) {
        if (isEmpty(result)) {
            return false;
        }
        return result.matches(IDCard.REGEX_ID_CARD15);
    }

    private static boolean isIDCard18(String result) {
        if (isEmpty(result)) {
            return false;
        }
        return result.matches(IDCard.REGEX_ID_CARD18);
    }

    public static boolean isEmpty(String result) {
        return result == null || result.isEmpty();
    }
}
