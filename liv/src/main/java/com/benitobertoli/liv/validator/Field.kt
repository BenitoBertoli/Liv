package com.benitobertoli.liv.validator

import io.reactivex.Observable

interface Field {
    val textChanges: Observable<CharSequence>?
    val focusChanges: Observable<Boolean>?
    fun getText(): CharSequence
    fun setError(error: CharSequence)
    fun removeError()
}