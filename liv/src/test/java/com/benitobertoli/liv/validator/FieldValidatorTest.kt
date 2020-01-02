package com.benitobertoli.liv.validator

import com.benitobertoli.liv.rule.Rule
import com.benitobertoli.liv.scheduler.SchedulerProvider
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit

class FieldValidatorTest {

    companion object {
        private const val VALIDATION_DELAY = 50L
        private const val ERROR_MESSAGE = "error message"
    }

    private val alwaysValidRule = mock<Rule> {
        on { isValid(any()) }.thenReturn(true)
        on { errorMessage }.thenReturn(ERROR_MESSAGE)
    }

    private val alwaysInvalidRule = mock<Rule> {
        on { isValid(any()) }.thenReturn(false)
        on { errorMessage }.thenReturn(ERROR_MESSAGE)
    }

    private val testScheduler = TestScheduler()
    private var schedulerProvider = mock<SchedulerProvider> {
        on { backgroundScheduler }.thenReturn(testScheduler)
        on { foregroundScheduler }.thenReturn(testScheduler)
    }

    private val textChanges = PublishSubject.create<CharSequence>()
    private val focusChanges = PublishSubject.create<Boolean>()
    private val field = FieldImpl(textChanges, focusChanges)

    @Test
    fun `invalid input`() {
        val sut = FieldValidator(
                schedulerProvider,
                field,
                // given
                listOf(alwaysInvalidRule),
                ValidationTime.AFTER,
                MessageType.NONE
        )
        // when
        sut.validate()
        // then
        assertThat(sut.value).isEqualTo(ValidatorState.INVALID)
    }

    @Test
    fun `valid input`() {
        val sut = FieldValidator(
                schedulerProvider,
                field,
                // given
                listOf(alwaysValidRule),
                ValidationTime.AFTER,
                MessageType.NONE
        )

        // when
        sut.validate()
        // then
        assertThat(sut.value).isEqualTo(ValidatorState.VALID)
    }

    @Test
    fun `LIVE validation after delay`() {
        // given
        val sut = FieldValidator(
                schedulerProvider,
                field,
                listOf(alwaysInvalidRule),
                ValidationTime.LIVE,
                MessageType.NONE,
                VALIDATION_DELAY
        )
        // when
        textChanges.onNext("A")
        // then
        assertValidatedAfterDelay(sut)
    }

    private fun assertValidatedAfterDelay(validator: FieldValidator) {
        assertThat(validator.value).isEqualTo(ValidatorState.NOT_VALIDATED)
        testScheduler.advanceTimeBy(VALIDATION_DELAY, TimeUnit.MILLISECONDS)
        assertThat(validator.value).isNotEqualTo(ValidatorState.NOT_VALIDATED)
    }

    @Test
    fun `no validation on focus gain`() {
        // given
        val sut = FieldValidator(
                schedulerProvider,
                field,
                listOf(alwaysInvalidRule),
                ValidationTime.AFTER,
                MessageType.NONE
        )
        // when
        focusChanges.onNext(true)
        testScheduler.triggerActions()
        // then
        assertThat(sut.value).isEqualTo(ValidatorState.NOT_VALIDATED)
    }

    @Test
    fun `validation on focus loss`() {
        // given
        val sut = FieldValidator(
                schedulerProvider,
                field,
                listOf(alwaysInvalidRule),
                ValidationTime.AFTER,
                MessageType.NONE
        )
        // when
        focusChanges.onNext(true)
        focusChanges.onNext(false)
        testScheduler.triggerActions()
        // then
        assertThat(sut.value).isEqualTo(ValidatorState.INVALID)
    }

    @Test
    fun `text changes on AFTER will reset validation`() {
        // given
        val sut = FieldValidator(
                schedulerProvider,
                field,
                listOf(alwaysInvalidRule),
                ValidationTime.AFTER,
                MessageType.NONE,
                VALIDATION_DELAY
        )
        sut.validate()
        assertThat(sut.value).isEqualTo(ValidatorState.INVALID)
        // when
        textChanges.onNext("A")
        testScheduler.advanceTimeBy(VALIDATION_DELAY, TimeUnit.MILLISECONDS)
        // then
        assertThat(sut.value).isEqualTo(ValidatorState.NOT_VALIDATED)
    }
}