package com.benitobertoli.liv.rule

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PatternRuleTest {

    companion object {
        private const val REGEX_ALPHABETIC = "^[a-zA-Z]*$"
    }

    @Test
    fun `valid input`() {
        // given
        val validInput = "abcABC"
        // when
        val sut = PatternRule("", REGEX_ALPHABETIC)
        assertThat(sut.isValid(validInput))
                // then
                .isTrue()
    }

    @Test
    fun `invalid input`() {
        // given
        val invalidInput = "123"
        // when
        val sut = PatternRule("", REGEX_ALPHABETIC)
        assertThat(sut.isValid(invalidInput))
                // then
                .isFalse()
    }

    @Test
    fun `ignore blank input`() {
        // given
        val blankInput = " "
        // when
        val sut = PatternRule("", REGEX_ALPHABETIC, ignoreBlank = true)
        assertThat(sut.isValid(blankInput))
                // then
                .isTrue()
    }

    @Test
    fun `do not ignore blank input`() {
        // given
        val blankInput = " "
        // when
        val sut = PatternRule("", REGEX_ALPHABETIC, ignoreBlank = false)
        assertThat(sut.isValid(blankInput))
                // then
                .isFalse()
    }
}