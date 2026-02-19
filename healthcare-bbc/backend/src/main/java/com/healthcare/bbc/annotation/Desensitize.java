package com.healthcare.bbc.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {
    DesensitizeType type() default DesensitizeType.PHONE;

    enum DesensitizeType {
        PHONE,
        EMAIL,
        ID_CARD,
        NAME,
        BANK_CARD,
        ADDRESS
    }
}
