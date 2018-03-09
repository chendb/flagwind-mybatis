package com.flagwind.persistent.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flagwind.persistent.model.ClauseOperator;


@Repeatable(ConditionOperators.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, FIELD })
public @interface ConditionOperator {
	

	String name() default "";
	

	ClauseOperator operator() default ClauseOperator.Equal;
}
