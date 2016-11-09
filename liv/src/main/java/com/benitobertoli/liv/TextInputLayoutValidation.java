package com.benitobertoli.liv;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

import com.benitobertoli.liv.validator.Validator;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.benitobertoli.liv.MessageType.SINGLE;
import static com.benitobertoli.liv.ValidationTime.AFTER;

public class TextInputLayoutValidation {
    public static final int DEBOUNCE_TIMEOUT_MILLIS = 500;

    private WeakReference<TextInputLayout> input;
    private ValidationTime time;
    private MessageType messageType;
    private List<Validator> validators;
    private Subscription subscription;
    private boolean gainedFocus = false;

    private ArrayList<String> errorMessages = new ArrayList<>();

    public TextInputLayoutValidation(TextInputLayout input, ValidationTime time, MessageType messageType, List<Validator> validators) {
        this.input = new WeakReference<>(input);
        this.time = time;
        this.validators = validators;
        this.messageType = messageType;
        init();
    }

    public TextInputLayoutValidation(TextInputLayout input, ValidationTime time, List<Validator> validators) {
        this(input, time, SINGLE, validators);
    }

    public TextInputLayoutValidation(TextInputLayout input, MessageType messageType, List<Validator> validators) {
        this(input, AFTER, messageType, validators);
    }

    public TextInputLayoutValidation(TextInputLayout input, List<Validator> validators) {
        this(input, AFTER, SINGLE, validators);
    }

    private void init() {
        TextInputLayout textInputLayout = input.get();
        if (textInputLayout == null || textInputLayout.getEditText() == null) {
            return;
        }

        final EditText editText = textInputLayout.getEditText();

        switch (time) {
            case LIVE:
                // while typing
                // All property-based types emit the current value as documented.
                // We .skip(1) on these observables because we don't want the initial value
                subscription = RxTextView.textChanges(editText)
                        .skip(1)
                        .debounce(DEBOUNCE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<CharSequence>() {
                            @Override
                            public void call(CharSequence text) {
                                validate();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });

                break;

            case AFTER:
                // on lose focus only
                // views will gain and lose focus as they are being laid out
                // we validate views that had already registered a focus gain after setting up the validator
                subscription = RxView.focusChanges(editText)
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
                        .observeOn(AndroidSchedulers.mainThread())
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
                break;
        }
    }

    public boolean validate() {
        errorMessages.clear();
        boolean valid = true;

        TextInputLayout textInputLayout = input.get();
        if (textInputLayout == null || textInputLayout.getEditText() == null) {
            return true;
        }

        for (Validator validator : validators) {
            if (validator.isValid(textInputLayout.getEditText().getText())) {
                valid &= true;
            } else {
                valid &= false;
                errorMessages.add(validator.getErrorMessage());
            }

            if (!valid) {
                textInputLayout.setError(getErrorMessage());
            } else {
                if (textInputLayout.isErrorEnabled()) {
                    // must change error because setError ignores duplicate strings
                    textInputLayout.setError(null);
                    textInputLayout.setErrorEnabled(false);
                }
            }
        }
        return valid;
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
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
