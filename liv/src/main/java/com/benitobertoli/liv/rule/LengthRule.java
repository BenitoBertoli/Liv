package com.benitobertoli.liv.rule;

import android.support.annotation.NonNull;

import rx.Observable;

public class LengthRule extends BaseRule {

    private int minLength;
    private int maxLength;

    public LengthRule(int min, int max) {
        super("Minimum length: " + min + ", Maximum length: " + max);
        minLength = min;
        maxLength = max;
        if (minLength > maxLength && maxLength > 0) {
            minLength = max;
            maxLength = min;
        }
        setErrorMessage(buildErrorMessage());
    }

    public LengthRule(String errorMessage, int min, int max) {
        super(errorMessage);
        minLength = min;
        maxLength = max;
        if (minLength > maxLength && maxLength > 0) {
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
    public Observable<Boolean> isValid(@NonNull CharSequence text) {
        boolean valid;
        int length = text.length();
        if (minLength == 0 && maxLength == 0) {
            // will be ignored
            valid = true;
        } else if (minLength == 0) {
            valid = length <= maxLength;
        } else if (maxLength == 0) {
            valid = length >= minLength;
        } else {
            valid = length >= minLength && length <= maxLength;
        }
        return Observable.just(valid);
    }
}
