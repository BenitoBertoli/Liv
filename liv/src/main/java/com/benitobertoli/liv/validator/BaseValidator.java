package com.benitobertoli.liv.validator;

public abstract class BaseValidator implements Validator {

    private String errorMessage;

    public BaseValidator() {
        this("Invalid");
    }

    public BaseValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
