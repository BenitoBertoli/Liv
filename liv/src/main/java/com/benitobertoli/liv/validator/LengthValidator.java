package com.benitobertoli.liv.validator;

import android.support.annotation.NonNull;

public class LengthValidator extends BaseValidator {

    private int minLength;
    private int maxLength;

    public LengthValidator(int min, int max) {
        super("Minimum length: " + min + ", Maximum length: " + max);
        minLength = min;
        maxLength = max;
        if (minLength > maxLength) {
            minLength = max;
            maxLength = min;
        }
        setErrorMessage(buildErrorMessage());
    }

    public LengthValidator(String errorMessage, int min, int max) {
        super(errorMessage);
        minLength = min;
        maxLength = max;
        if (minLength > maxLength) {
            minLength = max;
            maxLength = min;
        }
    }

    private String buildErrorMessage() {
        if (minLength == 0 && maxLength == 0) {
            // will be ignored
            return "";
        } else if (minLength == 0) {
            return "Maximum length: " + maxLength;
        } else if (maxLength == 0) {
            return "Minimum length: " + minLength;
        } else {
            return "Minimum length: " + minLength + ", Maximum length: " + maxLength;
        }
    }

    @Override
    public boolean isValid(@NonNull CharSequence text) {
        int length = text.length();
        if (minLength == 0 && maxLength == 0) {
            // will be ignored
            return true;
        } else if (minLength == 0) {
            return length <= maxLength;
        } else if (maxLength == 0) {
            return length >= minLength;
        } else {
            return (length >= minLength && length <= maxLength);
        }
    }
}
