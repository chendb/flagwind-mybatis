package com.flagwind.persistent.annotation;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated
@Target({ TYPE, METHOD, FIELD })  
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationshipEntities {

	RelationshipEntity[] value();
}
