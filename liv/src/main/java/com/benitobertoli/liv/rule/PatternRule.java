package com.benitobertoli.liv.rule;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

import io.reactivex.Observable;

public class PatternRule extends BaseRule {

    protected Pattern pattern;

    public PatternRule(String errorMessage, Pattern pattern) {
        super(errorMessage);
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        this.pattern = pattern;
    }

    @Override
    public Observable<Boolean> isValid(@NonNull CharSequence text) {
        return Observable.just(pattern.matcher(text).matches());
    }
}
