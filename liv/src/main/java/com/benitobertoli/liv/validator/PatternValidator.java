package com.benitobertoli.liv.validator;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public class PatternValidator extends BaseValidator {

    protected Pattern pattern;

    public PatternValidator(String errorMessage, Pattern pattern) {
        super(errorMessage);
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        this.pattern = pattern;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text) {
        return pattern.matcher(text).matches();
    }
}
