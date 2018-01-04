package com.flagwind.persistent.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flagwind.persistent.model.ClauseOperator;



@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, FIELD })
public @interface ConditionOperator {
	

	String name() default "";
	

	ClauseOperator operator() default ClauseOperator.Equal;
}
