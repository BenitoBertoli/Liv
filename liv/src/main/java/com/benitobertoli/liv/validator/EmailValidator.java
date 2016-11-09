package com.benitobertoli.liv.validator;

public class EmailValidator extends PatternIgnoreEmptyValidator {

    public EmailValidator() {
        this("Invalid email address");
    }

    public EmailValidator(String errorMessage) {
        super(errorMessage, android.util.Patterns.EMAIL_ADDRESS);
    }
}
