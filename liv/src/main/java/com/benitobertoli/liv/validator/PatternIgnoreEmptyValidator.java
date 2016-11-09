package com.benitobertoli.liv.validator;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public class PatternIgnoreEmptyValidator extends PatternValidator {

    public PatternIgnoreEmptyValidator(String errorMessage, Pattern pattern) {
        super(errorMessage, pattern);
    }

    @Override
    public boolean isValid(@NonNull CharSequence text) {
        // if text is empty, we will consider it valid
        // another validator should be used if the field is required
        return text.length() == 0 || pattern.matcher(text).matches();
    }
}
