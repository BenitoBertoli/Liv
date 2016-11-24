package com.benitobertoli.liv;

import android.support.design.widget.TextInputLayout;

import com.benitobertoli.liv.rule.Rule;
import com.jakewharton.rxrelay.PublishRelay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.FuncN;

public class Liv {
    private List<TextInputLayoutValidator> inputLayoutValidators;
    private ValidatorState state = ValidatorState.NOT_VALIDATED;
    private Callback callback;

    private Liv() {
        // prevent instantiation outside of the object
    }

    private Liv(Builder builder) {
        inputLayoutValidators = builder.inputLayoutValidators;
        callback = builder.callback;
    }

    public void start() {
        ArrayList<PublishRelay<ValidatorState>> relays = new ArrayList<>(inputLayoutValidators.size());
        for (TextInputLayoutValidator validator : inputLayoutValidators) {
            relays.add(validator.getStateRelay());
        }

        Observable.combineLatest(relays,
                new FuncN<ValidatorState>() {
                    @Override
                    public ValidatorState call(Object... states) {
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
                    }
                })
                .subscribe(new Action1<ValidatorState>() {
                    @Override
                    public void call(ValidatorState validatorState) {
                        state = validatorState;
                        if (callback != null) {
                            callback.onStateChange(validatorState);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void validate() {
        for (TextInputLayoutValidator validator : inputLayoutValidators) {
            if (validator.getState() == ValidatorState.NOT_VALIDATED) {
                validator.validate();
            }
        }
    }

    public void onDestroy() {
        for (TextInputLayoutValidator validator : inputLayoutValidators) {
            validator.onDestroy();
        }
    }

    public ValidatorState getState() {
        return state;
    }

    public interface Callback {
        void onStateChange(ValidatorState state);
    }


    public static final class Builder {
        private List<TextInputLayoutValidator> inputLayoutValidators;
        private Callback callback;

        public Builder() {
            inputLayoutValidators = new ArrayList<>();
        }

        public Builder add(TextInputLayout input, ValidationTime time, MessageType messageType, Rule... rules) {
            inputLayoutValidators.add(new TextInputLayoutValidator(input, time, messageType, Arrays.asList(rules)));
            return this;
        }

        public Builder add(TextInputLayout input, ValidationTime time, Rule... rules) {
            inputLayoutValidators.add(new TextInputLayoutValidator(input, time, Arrays.asList(rules)));
            return this;
        }

        public Builder add(TextInputLayout input, MessageType messageType, Rule... rules) {
            inputLayoutValidators.add(new TextInputLayoutValidator(input, messageType, Arrays.asList(rules)));
            return this;
        }

        public Builder add(TextInputLayout input, Rule... rules) {
            inputLayoutValidators.add(new TextInputLayoutValidator(input, Arrays.asList(rules)));
            return this;
        }

        public Builder callback(Callback val) {
            callback = val;
            return this;
        }

        public Liv build() {
            return new Liv(this);
        }
    }
}
