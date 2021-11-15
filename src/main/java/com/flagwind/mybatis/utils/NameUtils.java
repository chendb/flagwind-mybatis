package com.flagwind.mybatis.utils;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.definition.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtils {

    public static String SINGLE_QUOTE="\"";
    public static String DOUBLE_QUOTE="`";


    public static String DOT=".";

    private static String getFormatName(Config config, String name) {
        if (name == null) {
            return null;
        }

        if (name.startsWith(SINGLE_QUOTE) || name.startsWith(DOUBLE_QUOTE) || name.indexOf(DOT) > 0) {
            return name;
        }
        switch (config.getNameQuote()) {
            case 1:
                return String.format("`%s`", name);
            case 2:
                return String.format("\"%s\"", name);
            default:
                return String.format("%s", name);
        }
    }
    /**
     *
     * @param config
     * @param name
     * @return
     */
    public static String formatName(Config config, String name) {
        if (name == null) {
            return null;
        }

        if (name.indexOf(DOT) > 0) {
            String[] arr = name.split("\\"+DOT);
            if (arr.length == 2) {
                return String.format("%s.%s", arr[0], getFormatName(config, arr[1]));
            }
            return name;
        } else {
            if (name.startsWith(SINGLE_QUOTE) || name.startsWith(DOUBLE_QUOTE)) {
                return name;
            }
        }
        return getFormatName(config, name);
    }


    /**
     * 根据指定的样式进行转换
     *
     * @param str
     * @param style
     * @return String
     */
    public static String convertByStyle(String str, Style style) {
        switch (style) {
            case camelhump:
                return camelhumpToUnderline(str);
            case uppercase:
                return str.toUpperCase();
            case lowercase:
                return str.toLowerCase();
            case camelhumpAndLowercase:
                return camelhumpToUnderline(str).toLowerCase();
            case camelhumpAndUppercase:
                return camelhumpToUnderline(str).toUpperCase();
            case normal:
            default:
                return str;
        }
    }

    /**
     * 将驼峰风格替换为下划线风格
     */
    private static String camelhumpToUnderline(String str) {
        final int size;
        final char[] chars;
        final StringBuilder sb = new StringBuilder(
                (size = (chars = str.toCharArray()).length) * 3 / 2 + 1);
        char c;
        for (int i = 0; i < size; i++) {
            c = chars[i];
            if (isUppercaseAlpha(c)) {
                sb.append('_').append(toLowerAscii(c));
            } else {
                sb.append(c);
            }
        }
        return sb.charAt(0) == '_' ? sb.substring(1) : sb.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     */
    private static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    private static boolean isUppercaseAlpha(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    private static boolean isLowercaseAlpha(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    private static char toUpperAscii(char c) {
        if (isLowercaseAlpha(c)) {
            c -= (char) 0x20;
        }
        return c;
    }

    private static char toLowerAscii(char c) {
        if (isUppercaseAlpha(c)) {
            c += (char) 0x20;
        }
        return c;
    }
}
