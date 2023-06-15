package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// This property is used for identifying fields that should be mapped as objects in Java has also many hidden fields
// which are read by reflection causing error. This annotation tells as which fields to map and which to ignore.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONProperty {
    String name() default "";
}