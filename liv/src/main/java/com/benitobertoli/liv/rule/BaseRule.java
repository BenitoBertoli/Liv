package com.benitobertoli.liv.rule;

public abstract class BaseRule implements Rule {

    private String errorMessage;

    public BaseRule() {
        this("Invalid");
    }

    public BaseRule(String errorMessage) {
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
