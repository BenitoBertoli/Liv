package com.benitobertoli.liv.rule;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

import rx.Observable;

public class PatternIgnoreEmptyRule extends PatternRule {

    public PatternIgnoreEmptyRule(String errorMessage, Pattern pattern) {
        super(errorMessage, pattern);
    }

    @Override
    public Observable<Boolean> isValid(@NonNull CharSequence text) {
        // if text is empty, we will consider it valid
        // another validator should be used if the field is required
        return Observable.just(text.length() == 0 || pattern.matcher(text).matches());
    }
}
