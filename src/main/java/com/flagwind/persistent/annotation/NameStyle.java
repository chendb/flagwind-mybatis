package com.flagwind.persistent.annotation;

import com.flagwind.mybatis.code.Style;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameStyle {

    Style value() default Style.normal;

}
