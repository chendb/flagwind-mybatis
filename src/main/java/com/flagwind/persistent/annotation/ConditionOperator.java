package com.flagwind.persistent.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.flagwind.persistent.model.ClauseOperator;



@Retention(RetentionPolicy.RUNTIME) 
@Target({METHOD}) 
public @interface ConditionOperator {
	

	public String name() default "";
	

	public ClauseOperator operator() default ClauseOperator.Equal;
}
