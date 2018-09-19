package com.benitobertoli.liv;

import android.support.design.widget.TextInputLayout;

import com.benitobertoli.liv.rule.Rule;
import com.benitobertoli.liv.validator.MessageType;
import com.benitobertoli.liv.validator.TextInputLayoutValidator;
import com.benitobertoli.liv.validator.ValidationTime;
import com.benitobertoli.liv.validator.Validator;
import com.benitobertoli.liv.validator.ValidatorState;
import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


public class Liv {
    private List<Validator> validators;
    private ValidatorState state = ValidatorState.NOT_VALIDATED;
    private boolean submitWhenValid = false;
    private Callback callback;
    private Action submitAction;
    private Disposable livDisposable;

    private Liv() {
        // prevent instantiation outside of the object
    }

    private Liv(Builder builder) {
        validators = builder.validators;
        callback = builder.callback;
        submitAction = builder.submitAction;
    }

    public void start() {
        ArrayList<PublishRelay<ValidatorState>> relays = new ArrayList<>(validators.size());
        for (Validator validator : validators) {
            relays.add(validator.getStateRelay());
        }

        livDisposable = Observable.combineLatest(relays,
                states -> {
                    boolean hasNotValidated = false;
                    boolean hasInvalid = false;

                    for (Object obj : states) {
                        ValidatorState state = (ValidatorState) obj;
                        if (state == ValidatorState.NOT_VALIDATED) {
                            hasNotValidated = true;
                        } else if (state == ValidatorState.VALIDATING) {
                            // one of the validator is currently validating
                            return ValidatorState.VALIDATING;
                        } else if (state == ValidatorState.INVALID) {
                            hasInvalid = true;
                        }
                    }

                    if (hasNotValidated) {
                        return ValidatorState.NOT_VALIDATED;
                    } else if (hasInvalid) {
                        return ValidatorState.INVALID;
                    }

                    return ValidatorState.VALID;
                })
                .subscribe(validatorState -> {
                    state = validatorState;
                    if (callback != null) {
                        callback.onStateChange(validatorState);
                    }

                    if (submitAction != null) {
                        if (submitWhenValid) {
                            if (state == ValidatorState.VALID) {
                                submitWhenValid = false;
                                submitAction.performAction();
                            } else if (state == ValidatorState.INVALID) {
                                submitWhenValid = false;
                            }
                        }
                    }
                }, Throwable::printStackTrace);
    }

    public void validate() {
        for (Validator validator : validators) {
            if (validator.getState() == ValidatorState.NOT_VALIDATED
                    || validator.getState() == ValidatorState.INVALID) {
                validator.validate();
            }
        }
    }

    /**
     * Perform submit action as soon as the form is {@link ValidatorState#VALID}.
     */
    public void submitWhenValid() {
        switch (getState()) {
            case NOT_VALIDATED:
            case INVALID:
                submitWhenValid = true;
                validate();
                break;
            case VALIDATING:
                submitWhenValid = true;
                break;
            case VALID:
                if (submitAction != null) {
                    submitAction.performAction();
                }
                break;
        }
    }

    public void dispose() {
        if (livDisposable != null && !livDisposable.isDisposed()) {
            livDisposable.dispose();
        }

        for (Validator validator : validators) {
            validator.dispose();
        }
    }

    public ValidatorState getState() {
        return state;
    }

    public interface Callback {
        void onStateChange(ValidatorState state);
    }

    public interface Action {
        void performAction();
    }


    public static final class Builder {
        private List<Validator> validators;
        private Callback callback;
        private Action submitAction;

        public Builder() {
            validators = new ArrayList<>();
        }

        public Builder add(Validator validator) {
            validators.add(validator);
            return this;
        }

        public Builder add(TextInputLayout input, ValidationTime time, MessageType messageType, Rule... rules) {
            add(new TextInputLayoutValidator(input, time, messageType, Arrays.asList(rules)));
            return this;
        }

        public Builder add(TextInputLayout input, ValidationTime time, Rule... rules) {
            add(new TextInputLayoutValidator(input, time, Arrays.asList(rules)));
            return this;
        }

        public Builder add(TextInputLayout input, MessageType messageType, Rule... rules) {
            add(new TextInputLayoutValidator(input, messageType, Arrays.asList(rules)));
            return this;
        }

        public Builder add(TextInputLayout input, Rule... rules) {
            add(new TextInputLayoutValidator(input, Arrays.asList(rules)));
            return this;
        }

        public Builder callback(Callback val) {
            callback = val;
            return this;
        }

        public Builder submitAction(Action val) {
            submitAction = val;
            return this;
        }

        public Liv build() {
            return new Liv(this);
        }
    }
}
