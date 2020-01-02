package com.benitobertoli.liv.rule

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotBlankRuleTest {
    private val sut = NotBlankRule("Required")

    @Test
    fun `empty input is invalid`() {
        // given
        val emptyInput = ""
        // when
        assertThat(sut.isValid(emptyInput))
                // then
                .isFalse()
    }

    @Test
    fun `blank input is invalid`() {
        // given
        val blankInput = "  "
        // when
        assertThat(sut.isValid(blankInput))
                // then
                .isFalse()
    }

    @Test
    fun `not blank input is valid`() {
        // given
        val input = "input"
        // when
        assertThat(sut.isValid(input))
                // then
                .isTrue()
    }
}