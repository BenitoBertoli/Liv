package com.benitobertoli.liv.rule

import java.util.regex.Pattern

open class PatternRule @JvmOverloads constructor(
        override val errorMessage: CharSequence,
        private val pattern: Pattern,
        private val ignoreBlank: Boolean = true)
    : Rule {

    @JvmOverloads
    constructor(errorMessage: CharSequence,
                regex: String,
                ignoreBlank: Boolean = true)
            : this(errorMessage, Pattern.compile(regex), ignoreBlank)

    override fun isValid(input: CharSequence): Boolean {
        val matches = pattern.matcher(input).matches()
        return matches || (ignoreBlank && input.isBlank())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PatternRule) return false

        if (errorMessage != other.errorMessage) return false
        if (pattern.pattern() != other.pattern.pattern()) return false
        if (ignoreBlank != other.ignoreBlank) return false

        return true
    }

    override fun hashCode(): Int {
        var result = errorMessage.hashCode()
        result = 31 * result + pattern.pattern().hashCode()
        result = 31 * result + ignoreBlank.hashCode()
        return result
    }


}