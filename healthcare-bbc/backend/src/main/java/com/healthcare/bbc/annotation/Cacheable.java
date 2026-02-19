package com.healthcare.bbc.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {
    String key() default "";

    long expire() default 30;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
