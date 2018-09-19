package com.benitobertoli.liv.rule;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import io.reactivex.Observable;


public class ConfirmPasswordRule extends BaseRule {

    private TextInputLayout passwordLayout;

    public ConfirmPasswordRule(TextInputLayout passwordLayout, final TextInputLayout confirmLayout) {
        super("Passwords do not match");
        this.passwordLayout = passwordLayout;
        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String confirm = confirmLayout.getEditText().getText().toString();
                if (!TextUtils.isEmpty(confirm)) {
                    // this will trigger the confirm password view's validation
                    confirmLayout.getEditText().setText(confirmLayout.getEditText().getText());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public Observable<Boolean> isValid(@NonNull CharSequence text) {
        return Observable.just(passwordLayout.getEditText().getText().toString().equals(text.toString()));
    }
}
