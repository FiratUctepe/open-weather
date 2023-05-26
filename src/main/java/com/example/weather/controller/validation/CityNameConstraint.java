package com.example.weather.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {CityNameValidator.class})
public @interface CityNameConstraint {

    String message() default "Doğrulanmamış şehir ismi";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
