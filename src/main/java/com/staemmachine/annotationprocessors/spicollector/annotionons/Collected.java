package com.staemmachine.annotationprocessors.spicollector.annotionons;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(value = {TYPE, METHOD})
public @interface Collected {

    boolean enabled() default true;
}
