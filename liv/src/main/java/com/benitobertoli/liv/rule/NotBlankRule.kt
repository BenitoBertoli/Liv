package com.benitobertoli.liv.rule

data class NotBlankRule(override val errorMessage: CharSequence) : Rule {
    override fun isValid(input: CharSequence): Boolean = input.isNotBlank()
}