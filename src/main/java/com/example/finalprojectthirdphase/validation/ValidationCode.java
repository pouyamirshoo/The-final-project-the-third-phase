package com.example.finalprojectthirdphase.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCode.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationCode {
    String message() default "Invalid nation code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
