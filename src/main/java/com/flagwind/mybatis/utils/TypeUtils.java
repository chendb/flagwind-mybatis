package com.flagwind.mybatis.utils;

public class TypeUtils {

    @SuppressWarnings("unchecked")
    public static <T> T castTo(Object obj) {
        return (T) obj;
    }

}
