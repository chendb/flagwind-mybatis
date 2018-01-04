package com.flagwind.persistent.annotation;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD, FIELD })
public @interface Relationship {
	String toTable() ;
}
