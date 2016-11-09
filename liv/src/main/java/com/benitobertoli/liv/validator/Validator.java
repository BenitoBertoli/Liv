package com.benitobertoli.liv.validator;

import android.support.annotation.NonNull;

public interface Validator {
    boolean isValid(@NonNull CharSequence text);

    String getErrorMessage();
}
