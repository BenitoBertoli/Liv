package com.benitobertoli.liv.validator;

import android.support.design.widget.TextInputLayout;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.EditText;

import com.benitobertoli.liv.rule.Rule;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.benitobertoli.liv.validator.MessageType.SINGLE;
import static com.benitobertoli.liv.validator.ValidationTime.AFTER;
import static com.benitobertoli.liv.validator.ValidationTime.LIVE;
import static com.benitobertoli.liv.validator.ValidatorState.INVALID;
import static com.benitobertoli.liv.validator.ValidatorState.NOT_VALIDATED;
import static com.benitobertoli.liv.validator.ValidatorState.VALID;
import static com.benitobertoli.liv.validator.ValidatorState.VALIDATING;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.newThread;

public class TextInputLayoutValidator extends Validator {
    private static final int DEBOUNCE_TIMEOUT_MILLIS = 500;

    private WeakReference<TextInputLayout> input;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean gainedFocus = false;

    private ValidationTime time;
    private List<Rule> rules;

    private MessageType messageType;
    private ArrayList<String> errorMessages = new ArrayList<>();

    public TextInputLayoutValidator(TextInputLayout input, ValidationTime time, MessageType messageType, List<Rule> rules) {
        super();
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
        this(input, LIVE, messageType, rules);
    }

    public TextInputLayoutValidator(TextInputLayout input, List<Rule> rules) {
        this(input, LIVE, SINGLE, rules);
    }

    private void init() {
        TextInputLayout textInputLayout = input.get();
        if (textInputLayout == null || textInputLayout.getEditText() == null) {
            return;
        }

        final EditText editText = textInputLayout.getEditText();

        // while typing
        // All property-based types emit the current value
        // We .skip(1) on these observables because we don't want the initial value
        compositeDisposable.add(RxTextView.textChanges(editText)
                .skip(1)
                .debounce(DEBOUNCE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(mainThread())
                .subscribe(text -> {
                    if (time == LIVE) {
                        validate();
                    } else {
                        setState(NOT_VALIDATED);
                    }
                }, Throwable::printStackTrace));


        if (time == AFTER) {
            // on lose focus only
            // views will gain and lose focus as they are being laid out
            // we validate views that had already registered a focus gain after setting up the validator
            compositeDisposable.add(RxView.focusChanges(editText)
                    .filter(hasFocus -> {
                        if (hasFocus) {
                            gainedFocus = true;
                            return false;
                        } else {
                            boolean hadFocus = gainedFocus;
                            gainedFocus = false;
                            return hadFocus;
                        }
                    })
                    .observeOn(mainThread())
                    .subscribe(unused -> validate(), Throwable::printStackTrace));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
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

        compositeDisposable.add(Observable.fromIterable(rules)
                .subscribeOn(newThread())
                // for each Rule, check if text is valid and pass Rule downstream
                .flatMap(rule -> rule.isValid(text).map(valid -> new Pair<>(rule, valid)))
                .observeOn(mainThread())
                // add error message in case of error
                .doOnNext(pair -> {
                    if (!pair.second) { // if not valid
                        errorMessages.add(pair.first.getErrorMessage());
                    }
                })
                // Rule object is not needed anymore
                .map(pair -> pair.second)
                // accumulate results in one boolean
                .reduce((first, second) -> first & second)
                .subscribe(valid -> {
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
                }, Throwable::printStackTrace));
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

    @Override
    public void dispose() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
