package com.flagwind.mybatis.entity.codes;

import com.flagwind.lang.CodeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author chendb
 * @description:
 * @date 2020-06-17 10:55:17
 */
@RequiredArgsConstructor
@Getter
public enum Sex implements CodeType {

    Woman("0", "女士"), Man("1", "男士"),
    Unknown("-1", "未知");

    private final String value;
    private final String text;
}


