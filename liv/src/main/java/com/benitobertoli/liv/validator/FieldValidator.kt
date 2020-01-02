package com.benitobertoli.liv.validator

import com.benitobertoli.liv.rule.Rule
import com.benitobertoli.liv.scheduler.SchedulerProvider
import com.benitobertoli.liv.validator.MessageFormatter.format
import com.benitobertoli.liv.validator.ValidationTime.AFTER
import com.benitobertoli.liv.validator.ValidationTime.LIVE
import com.benitobertoli.liv.validator.ValidatorState.INVALID
import com.benitobertoli.liv.validator.ValidatorState.NOT_VALIDATED
import com.benitobertoli.liv.validator.ValidatorState.VALID
import com.benitobertoli.liv.validator.ValidatorState.VALIDATING
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class FieldValidator @JvmOverloads constructor(
        private val schedulerProvider: SchedulerProvider,
        private val field: Field,
        private val rules: List<Rule>,
        private val time: ValidationTime = LIVE,
        private val messageType: MessageType = MessageType.SINGLE,
        private val validationDelayMillis: Long = DEFAULT_VALIDATION_DELAY
) : Validator() {

    companion object {
        const val DEFAULT_VALIDATION_DELAY = 500L
    }

    private val compositeDisposable = CompositeDisposable()
    private var gainedFocus = false
    private val errorMessages = mutableListOf<CharSequence>()
    private var nonBlankEncountered = false

    init {
        initTextChangeValidation()

        if (time == AFTER) {
            initFocusLossValidation()
        }
    }

    private fun initTextChangeValidation() {
        field?.textChanges?.let { textChanges ->
            compositeDisposable.add(textChanges
                    .debounce(validationDelayMillis, TimeUnit.MILLISECONDS, schedulerProvider.backgroundScheduler)
                    .filter { it.isNotBlank() || nonBlankEncountered }
                    .doOnNext { if (it.isNotBlank()) nonBlankEncountered = true }
                    .observeOn(schedulerProvider.foregroundScheduler)
                    .subscribe({
                        when (time) {
                            LIVE -> validate()
                            else -> value = NOT_VALIDATED
                        }
                    }, { it.printStackTrace() })
            )
        }
    }

    private fun initFocusLossValidation() {
        field.focusChanges?.let { focusChanges ->
            compositeDisposable.add(focusChanges
                    .filter { hasFocus ->
                        if (hasFocus) {
                            gainedFocus = true
                            false
                        } else {
                            val validate = gainedFocus
                            gainedFocus = false
                            validate
                        }
                    }
                    .observeOn(schedulerProvider.foregroundScheduler)
                    .subscribe({ validate() }, { it.printStackTrace() })
            )
        }

    }

    override fun validate() {
        errorMessages.clear()
        value = VALIDATING

        var allValid = true
        var valid: Boolean
        rules.forEach {
            valid = it.isValid(field.getText())
            if (!valid)
                errorMessages.add(it.errorMessage)
            allValid = allValid and valid
        }
        value = if (allValid) {
            field.removeError()
            VALID
        } else {
            field.setError(format(errorMessages, messageType))
            INVALID
        }
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }
}
