package com.example.finalprojectthirdphase.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCode implements ConstraintValidator<ValidationCode, String> {
    @Override
    public void initialize(ValidationCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(String nationalCode, ConstraintValidatorContext constraintValidatorContext) {
        if (!nationalCode.matches("^\\d{10}$"))
            return false;

        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nationalCode.charAt(i)) * (10 - i);
        }

        int lastDigit = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));
        int divideRemaining = sum % 11;

        return ((divideRemaining < 2 && lastDigit == divideRemaining) || (divideRemaining >= 2 && (11 - divideRemaining) == lastDigit));
    }
}
