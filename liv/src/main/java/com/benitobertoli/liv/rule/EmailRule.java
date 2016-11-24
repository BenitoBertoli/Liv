package com.benitobertoli.liv.rule;

public class EmailRule extends PatternIgnoreEmptyRule {

    public EmailRule() {
        this("Invalid email address");
    }

    public EmailRule(String errorMessage) {
        super(errorMessage, android.util.Patterns.EMAIL_ADDRESS);
    }
}
