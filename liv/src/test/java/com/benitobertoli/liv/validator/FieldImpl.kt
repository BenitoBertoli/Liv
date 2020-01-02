package com.benitobertoli.liv.validator

import io.reactivex.Observable

class FieldImpl(
        override val textChanges: Observable<CharSequence>,
        override val focusChanges: Observable<Boolean>
) : Field {
    var errorMessage: CharSequence? = null

    override fun getText(): CharSequence = "X"
    override fun setError(error: CharSequence) {
        errorMessage = error
    }

    override fun removeError() {
        errorMessage = null
    }
}