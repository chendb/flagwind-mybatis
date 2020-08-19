package com.flagwind.mybatis.tenant;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tenant {

    String value() default "tenantId";

    TenantStrategy strategy() default TenantStrategy.Private;

    VisitMode mode() default VisitMode.Pessimistic;


    enum TenantStrategy {
        Private, Children, Childrens
    }

    enum VisitMode {
        /**
         * 悲观的
         */
        Pessimistic,
        /**
         * 中立的
         */
        Neutral,
        /**
         * 乐观的
         */
        Optimistic
    }

}
