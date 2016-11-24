package com.benitobertoli.liv;

import android.support.design.widget.TextInputLayout;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.EditText;

import com.benitobertoli.liv.rule.Rule;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxrelay.PublishRelay;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.benitobertoli.liv.MessageType.SINGLE;
import static com.benitobertoli.liv.ValidationTime.AFTER;
import static com.benitobertoli.liv.ValidationTime.LIVE;
import static com.benitobertoli.liv.ValidatorState.INVALID;
import static com.benitobertoli.liv.ValidatorState.NOT_VALIDATED;
import static com.benitobertoli.liv.ValidatorState.VALID;
import static com.benitobertoli.liv.ValidatorState.VALIDATING;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.newThread;

public class TextInputLayoutValidator {
    public static final int DEBOUNCE_TIMEOUT_MILLIS = 500;

    private WeakReference<TextInputLayout> input;
    private Subscription textChangeSubscription;
    private Subscription focusChangeSubscription;
    private Subscription validateSubscription;
    private boolean gainedFocus = false;

    // base
    private ValidatorState state = NOT_VALIDATED;
    private PublishRelay<ValidatorState> stateRelay;
    private ValidationTime time;
    private List<Rule> rules;

    private MessageType messageType;
    private ArrayList<String> errorMessages = new ArrayList<>();

    public TextInputLayoutValidator(TextInputLayout input, ValidationTime time, MessageType messageType, List<Rule> rules) {
        this.input = new WeakReference<>(input);
        this.time = time;
        this.rules = rules;
        this.messageType = messageType;
        init();
    }

    public TextInputLayoutValidator(TextInputLayout input, ValidationTime time, List<Rule> rules) {
        this(input, time, SINGLE, rules);
    }

    public TextInputLayoutValidator(TextInputLayout input, MessageType messageType, List<Rule> rules) {
        this(input, AFTER, messageType, rules);
    }

    public TextInputLayoutValidator(TextInputLayout input, List<Rule> rules) {
        this(input, AFTER, SINGLE, rules);
    }

    private void init() {
        TextInputLayout textInputLayout = input.get();
        if (textInputLayout == null || textInputLayout.getEditText() == null) {
            return;
        }

        stateRelay = PublishRelay.create();

        final EditText editText = textInputLayout.getEditText();

        // while typing
        // All property-based types emit the current value
        // We .skip(1) on these observables because we don't want the initial value
        textChangeSubscription = RxTextView.textChanges(editText)
                .skip(1)
                .debounce(DEBOUNCE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence text) {
                        if (time == LIVE) {
                            validate();
                        } else {
                            setState(NOT_VALIDATED);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


        if (time == AFTER) {
            // on lose focus only
            // views will gain and lose focus as they are being laid out
            // we validate views that had already registered a focus gain after setting up the validator
            focusChangeSubscription = RxView.focusChanges(editText)
                    .filter(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean hasFocus) {
                            if (hasFocus) {
                                gainedFocus = true;
                                return false;
                            } else {
                                boolean hadFocus = gainedFocus;
                                gainedFocus = false;
                                return hadFocus;
                            }
                        }
                    })
                    .observeOn(mainThread())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            validate();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }
    }

    public void validate() {
        final TextInputLayout textInputLayout = input.get();
        if (textInputLayout == null || textInputLayout.getEditText() == null) {
            // textInputLayout was garbage collected
            // which probably means the UI is no longer in use and validation is no longer needed
            return;
        }

        errorMessages.clear();
        setState(VALIDATING);

        final String text = textInputLayout.getEditText().getText().toString();

        validateSubscription = Observable.from(rules)
                .subscribeOn(newThread())
                // for each Rule, check if text is valid and pass Rule downstream
                .flatMap(new Func1<Rule, Observable<Pair<Rule, Boolean>>>() {
                    @Override
                    public Observable<Pair<Rule, Boolean>> call(final Rule rule) {
                        return rule.isValid(text).map(new Func1<Boolean, Pair<Rule, Boolean>>() {
                            @Override
                            public Pair<Rule, Boolean> call(Boolean valid) {
                                return new Pair<>(rule, valid);
                            }
                        });
                    }
                })
                .observeOn(mainThread())
                // add error message in case of error
                .doOnNext(new Action1<Pair<Rule, Boolean>>() {
                    @Override
                    public void call(Pair<Rule, Boolean> pair) {
                        if (!pair.second) {
                            errorMessages.add(pair.first.getErrorMessage());
                        }
                    }
                })
                // Rule object is not needed anymore
                .map(new Func1<Pair<Rule, Boolean>, Boolean>() {

                    @Override
                    public Boolean call(Pair<Rule, Boolean> pair) {
                        return pair.second;
                    }
                })
                // accumulate results in one boolean
                .reduce(new Func2<Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean first, Boolean second) {
                        return first & second;
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean valid) {
                        if (!valid) {
                            textInputLayout.setError(getErrorMessage());
                        } else {
                            if (textInputLayout.isErrorEnabled()) {
                                // must change error because setError ignores duplicate strings
                                textInputLayout.setError(null);
                                textInputLayout.setErrorEnabled(false);
                            }
                        }
                        setState(valid ? VALID : INVALID);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private String getErrorMessage() {
        switch (messageType) {
            case NONE:
            default:
                return "";

            case SINGLE:
                if (errorMessages.size() > 0) {
                    return errorMessages.get(0);
                } else {
                    return "";
                }

            case MULTIPLE:
                return TextUtils.join("\n", errorMessages);
        }
    }

    public void onDestroy() {
        if (textChangeSubscription != null && !textChangeSubscription.isUnsubscribed()) {
            textChangeSubscription.unsubscribe();
        }

        if (focusChangeSubscription != null && !focusChangeSubscription.isUnsubscribed()) {
            focusChangeSubscription.unsubscribe();
        }

        if (validateSubscription != null && !validateSubscription.isUnsubscribed()) {
            validateSubscription.unsubscribe();
        }
    }

    public PublishRelay<ValidatorState> getStateRelay() {
        return stateRelay;
    }

    public ValidatorState getState() {
        return state;
    }

    private void setState(ValidatorState state) {
        this.state = state;
        stateRelay.call(state);
    }
}
