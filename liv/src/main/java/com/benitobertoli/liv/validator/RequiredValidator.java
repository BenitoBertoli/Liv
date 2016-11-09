package com.benitobertoli.liv.validator;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class RequiredValidator extends BaseValidator {

    public RequiredValidator() {
        this("Required");
    }

    public RequiredValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public boolean isValid(@NonNull CharSequence text) {
        return !TextUtils.isEmpty(text.toString().trim());
    }
}
