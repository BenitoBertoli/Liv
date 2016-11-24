package com.benitobertoli.liv.rule;

import android.support.annotation.NonNull;

import rx.Observable;

public interface Rule {
    Observable<Boolean> isValid(@NonNull CharSequence text);

    String getErrorMessage();
}
