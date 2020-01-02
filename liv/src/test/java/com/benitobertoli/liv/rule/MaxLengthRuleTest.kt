package com.benitobertoli.liv.rule

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MaxLengthRuleTest {

    private val sut = MaxLengthRule("error-message", 5)

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
    fun `greater length invalid`() {
        // given
        val eight = "12345678"
        // when
        assertThat(sut.isValid(eight))
                // then
                .isFalse()
    }

    @Test
    fun `smaller length valid`() {
        // given
        val four = "1234"
        // when
        assertThat(sut.isValid(four))
                // then
                .isTrue()
    }
}