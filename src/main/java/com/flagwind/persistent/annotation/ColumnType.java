package com.flagwind.persistent.annotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnType {
    String column() default "";

    JdbcType jdbcType() default JdbcType.UNDEFINED;

    Class<? extends TypeHandler<?>> typeHandler() default UnknownTypeHandler.class;
}