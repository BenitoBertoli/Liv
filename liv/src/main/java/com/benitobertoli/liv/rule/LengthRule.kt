package com.benitobertoli.liv.rule

data class LengthRule(
        override val errorMessage: CharSequence,
        private val lengths: List<Int>
) : Rule {
    constructor(errorMessage: CharSequence, vararg length: Int) : this(errorMessage, length.toList())

    override fun isValid(input: CharSequence): Boolean = lengths.contains(input.trim().length)
}