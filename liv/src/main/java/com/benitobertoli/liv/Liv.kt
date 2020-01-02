package com.benitobertoli.liv

import com.benitobertoli.liv.scheduler.SchedulerProvider
import com.benitobertoli.liv.scheduler.SchedulerProviderImpl
import com.benitobertoli.liv.validator.Validator
import com.benitobertoli.liv.validator.ValidatorState
import com.benitobertoli.liv.validator.ValidatorState.INVALID
import com.benitobertoli.liv.validator.ValidatorState.NOT_VALIDATED
import com.benitobertoli.liv.validator.ValidatorState.VALID
import com.benitobertoli.liv.validator.ValidatorState.VALIDATING
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


class Liv(
        private val schedulerProvider: SchedulerProvider = SchedulerProviderImpl,
        validators: List<Validator> = listOf(),
        private var submitAction: (() -> Unit)? = null,
        private var stateChanges: ((ValidatorState) -> Unit)? = null
) {
    private val validators: MutableList<Validator> = validators.toMutableList()
    private var validatorState = NOT_VALIDATED
    private var submitWhenValid = false
    private var livDisposable = CompositeDisposable()

    // Java compatibility
    constructor(builder: Builder) : this(builder.schedulerProvider, builder.validators, builder.submitAction, builder.stateChanges)

    fun add(validator: Validator) {
        validators.add(validator)
    }

    fun callback(stateChanges: ((ValidatorState) -> Unit)?) {
        this.stateChanges = stateChanges
    }

    fun submitAction(submitAction: (() -> Unit)?) {
        this.submitAction = submitAction
    }

    fun start() {
        livDisposable.add(
                Observable.combineLatest(validators.map { it.state }) { states -> resolveState(states) }
                        .subscribeOn(schedulerProvider.foregroundScheduler)
                        .observeOn(schedulerProvider.foregroundScheduler)
                        .subscribe({ state ->
                            validatorState = state
                            stateChanges?.invoke(validatorState)
                            submitAction?.let { invokeActionIfValid(it) }
                        }, { it.printStackTrace() }))
    }

    private fun resolveState(states: Array<out Any>): ValidatorState {
        var hasNotValidated = false
        var hasInvalid = false

        states.forEach { state ->
            when (state) {
                NOT_VALIDATED -> hasNotValidated = true
                VALIDATING -> return VALIDATING
                INVALID -> hasInvalid = true
                else -> {
                }
            }
        }

        if (hasNotValidated) {
            return NOT_VALIDATED
        } else if (hasInvalid) {
            return INVALID
        }

        return VALID
    }

    private fun invokeActionIfValid(action: () -> Unit) {
        if (submitWhenValid) {
            when (validatorState) {
                VALID -> {
                    submitWhenValid = false
                    action()
                }
                INVALID -> submitWhenValid = false
                else -> {
                }
            }
        }
    }

    private fun validate() {
        validators
                .filter { it.value == NOT_VALIDATED || it.value == INVALID }
                .forEach { it.validate() }
    }

    fun submitWhenValid() {
        when (validatorState) {
            NOT_VALIDATED, INVALID -> {
                submitWhenValid = true
                validate()
            }
            VALIDATING -> submitWhenValid = true
            VALID -> submitAction?.invoke()
        }
    }

    fun dispose() {
        livDisposable.dispose()

        for (validator in validators) {
            validator.dispose()
        }
    }

    // Java compatibility
    class Builder(val schedulerProvider: SchedulerProvider = SchedulerProviderImpl) {
        val validators = mutableListOf<Validator>()
        var submitAction: (() -> Unit)? = null
        var stateChanges: ((ValidatorState) -> Unit)? = null

        fun add(validator: Validator): Builder {
            validators.add(validator)
            return this
        }

        fun submitAction(action: (() -> Unit)?): Builder {
            submitAction = action
            return this
        }

        fun stateChanges(stateChanges: ((ValidatorState) -> Unit)?): Builder {
            this.stateChanges = stateChanges
            return this
        }

        fun build(): Liv {
            return Liv(this)
        }
    }
}
