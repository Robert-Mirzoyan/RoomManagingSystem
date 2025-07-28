package com.example.project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = RoomNameValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidRoomName {
    String message() default "Room name must start with a capital letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
