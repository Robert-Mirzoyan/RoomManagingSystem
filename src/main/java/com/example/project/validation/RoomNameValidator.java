package com.example.project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoomNameValidator implements ConstraintValidator<ValidRoomName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty() && Character.isUpperCase(value.charAt(0));
    }
}
