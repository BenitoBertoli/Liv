package com.benitobertoli.liv.rule

interface Rule {
    fun isValid(input: CharSequence): Boolean
    val errorMessage: CharSequence
}