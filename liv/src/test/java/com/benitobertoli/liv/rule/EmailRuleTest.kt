package com.benitobertoli.liv.rule

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EmailRuleTest {

    private val sut = EmailRule("Invalid email")

    @Test
    fun `valid email`() {
        // given
        val correctEmail = "email@example.com"

        // when
        assertThat(sut.isValid(correctEmail))
                // then
                .isTrue()
    }

    @Test
    fun `invalid email`() {
        // given
        val incorrectEmail = "email@example"

        // when
        assertThat(sut.isValid(incorrectEmail))
                // then
                .isFalse()
    }

}