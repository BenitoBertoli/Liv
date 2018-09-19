package com.benitobertoli.liv.rule;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import io.reactivex.Observable;

public class NotEmptyRule extends BaseRule {

    public NotEmptyRule() {
        this("Required");
    }

    public NotEmptyRule(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Observable<Boolean> isValid(@NonNull CharSequence text) {
        return Observable.just(!TextUtils.isEmpty(text.toString().trim()));
    }
}
