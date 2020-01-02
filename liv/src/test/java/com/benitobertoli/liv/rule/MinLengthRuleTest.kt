package com.benitobertoli.liv.rule

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MinLengthRuleTest {

    private val sut = MinLengthRule("error-message", 5)

    @Test
    fun `exact length valid`() {
        // given
        val five = "12345"
        // when
        assertThat(sut.isValid(five))
                // then
                .isTrue()
    }

    @Test
    fun `greater length valid`() {
        // given
        val eight = "12345678"
        // when
        assertThat(sut.isValid(eight))
                // then
                .isTrue()
    }

    @Test
    fun `smaller length invalid`() {
        // given
        val four = "1234"
        // when
        assertThat(sut.isValid(four))
                // then
                .isFalse()
    }
}