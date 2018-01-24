package com.flagwind.persistent.annotation;

import com.flagwind.persistent.AggregateType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 聚合字段定义
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
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
