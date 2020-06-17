package com.flagwind.mybatis.entity.codes;

import com.flagwind.lang.CodeType;

/**
 * @author chendb
 * @description:
 * @date 2020-06-17 10:55:17
 */
public class FocusType implements CodeType {

    private String value;
    private String text;

    public FocusType() {

    }

    public FocusType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }
}
