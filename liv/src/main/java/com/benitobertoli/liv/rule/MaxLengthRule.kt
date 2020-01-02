package com.benitobertoli.liv.rule

data class MaxLengthRule(
        override val errorMessage: CharSequence,
        private val maxLength: Int
) : Rule {
    override fun isValid(input: CharSequence): Boolean = input.trim().length <= maxLength
}