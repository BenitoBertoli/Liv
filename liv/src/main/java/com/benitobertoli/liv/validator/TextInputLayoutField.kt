package com.benitobertoli.liv.validator

import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable

class TextInputLayoutField(private val textInputLayout: TextInputLayout) : Field {
    override val textChanges: Observable<CharSequence>? = textInputLayout.editText?.textChanges()
    override val focusChanges: Observable<Boolean>? = textInputLayout.editText?.focusChanges()
    override fun getText(): CharSequence = textInputLayout.editText?.text ?: ""
    override fun setError(error: CharSequence) {
        textInputLayout.error = error.toString()
    }

    override fun removeError() {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
    }
}