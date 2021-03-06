package com.ovit.nswy.frame.common.annotation;

import java.lang.annotation.*;

/**
 * Created by ${huipei.x} on 2017-10-16.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface BaseException {
    String value() default "";
    String description()  default "";
}
