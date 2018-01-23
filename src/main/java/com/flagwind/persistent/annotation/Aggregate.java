package com.flagwind.persistent.annotation;

import com.flagwind.persistent.AggregateType;

/**
 * 聚合字段定义
 */
public @interface Aggregate {

    /**
     * 聚合类型
     * @return
     */
    AggregateType type() default AggregateType.Count;

    /**
     * 当没有指定对应的列名时，将把字段名作为列名
     * @return
     */
    String column() default "";
}
