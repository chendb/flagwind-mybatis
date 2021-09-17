package com.flagwind.mybatis.definition.scripting.annotation;

import java.lang.annotation.*;

/**
 * 公式名映射
 *
 * @author 奔波儿灞
 * @since 1.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {


    /**
     * 公式名，必须唯一
     *
     * @return 公式名
     */
    String name();

    /**
     * 公式描述
     *
     * @return 描述
     */
    String description();

    /**
     * 公式使用例子
     *
     * @return 使用例子
     */
    String usage();



}
