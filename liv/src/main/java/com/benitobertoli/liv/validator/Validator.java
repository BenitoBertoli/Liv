package com.benitobertoli.liv.validator;

import com.jakewharton.rxrelay2.PublishRelay;

import static com.benitobertoli.liv.validator.ValidatorState.NOT_VALIDATED;

public abstract class Validator {

    private ValidatorState state = NOT_VALIDATED;
    private PublishRelay<ValidatorState> stateRelay;

    public Validator() {
        stateRelay = PublishRelay.create();
    }

    public abstract void validate();

    public abstract void dispose();

    public PublishRelay<ValidatorState> getStateRelay() {
        return stateRelay;
    }

    public ValidatorState getState() {
        return state;
    }

    protected void setState(ValidatorState state) {
        this.state = state;
        stateRelay.accept(state);
    }

}
