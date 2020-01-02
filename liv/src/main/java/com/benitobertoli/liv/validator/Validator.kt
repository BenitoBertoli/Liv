package com.benitobertoli.liv.validator

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


abstract class Validator {
    var value = ValidatorState.NOT_VALIDATED
        set(value) {
            field = value
            _state.onNext(value)
        }

    private val _state = PublishSubject.create<ValidatorState>()
    val state: Observable<ValidatorState> = _state

    abstract fun validate()

    abstract fun dispose()
}
