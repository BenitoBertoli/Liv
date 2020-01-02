package com.benitobertoli.liv.rule

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LengthRuleTest {
    companion object {
        private const val ERROR_MESSAGE = "error-message"
    }

    @Test
    fun `single length valid`() {
        // given
        val nine = "123456789"
        // when
        val sut = LengthRule(ERROR_MESSAGE, 9)
        assertThat(sut.isValid(nine))
                // then
                .isTrue()
    }

    @Test
    fun `single length invalid`() {
        // given
        val nine = "123456789"
        // when
        val sut = LengthRule(ERROR_MESSAGE, 8)
        assertThat(sut.isValid(nine))
                // then
                .isFalse()
    }

    @Test
    fun `multiple lengths valid`() {
        // given
        val nine = "123456789"
        val ten = "1234567890"

        // when
        val sut = LengthRule(ERROR_MESSAGE, 9, 10)
        assertThat(sut.isValid(nine))
                // then
                .isTrue()

        assertThat(sut.isValid(ten))
                // then
                .isTrue()
    }

    @Test
    fun `multiple lengths invalid`() {
        // given
        val six = "123456"
        // when
        val sut = LengthRule(ERROR_MESSAGE, 9, 10)
        assertThat(sut.isValid(six))
                // then
                .isFalse()
    }
}