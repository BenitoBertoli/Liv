package com.benitobertoli.liv.rule

data class MinLengthRule(
        override val errorMessage: CharSequence,
        private val minLength: Int
) : Rule {
    override fun isValid(input: CharSequence): Boolean = input.trim().length >= minLength
}