package com.benitobertoli.liv;

import android.support.design.widget.TextInputLayout;

import com.benitobertoli.liv.validator.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Liv {
    private List<TextInputLayoutValidation> inputLayoutValidations;

    public Liv() {
        inputLayoutValidations = new ArrayList<>();
    }

    public Liv add(TextInputLayout input, ValidationTime time, MessageType messageType, Validator... validators) {
        inputLayoutValidations.add(new TextInputLayoutValidation(input, time, messageType, Arrays.asList(validators)));
        return this;
    }

    public Liv add(TextInputLayout input, ValidationTime time, Validator... validators) {
        inputLayoutValidations.add(new TextInputLayoutValidation(input, time, Arrays.asList(validators)));
        return this;
    }

    public Liv add(TextInputLayout input, MessageType messageType, Validator... validators) {
        inputLayoutValidations.add(new TextInputLayoutValidation(input, messageType, Arrays.asList(validators)));
        return this;
    }

    public Liv add(TextInputLayout input, Validator... validators) {
        inputLayoutValidations.add(new TextInputLayoutValidation(input, Arrays.asList(validators)));
        return this;
    }

    public boolean validate() {
        boolean valid = true;
        for (TextInputLayoutValidation field : inputLayoutValidations) {
            valid &= field.validate();
        }
        return valid;
    }

    public void onDestroy() {
        for (TextInputLayoutValidation field : inputLayoutValidations) {
            field.onDestroy();
        }
    }
}
