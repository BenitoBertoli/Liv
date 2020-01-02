package com.benitobertoli.liv

import com.benitobertoli.liv.rx.RxSynchronizeSchedulersRule
import com.benitobertoli.liv.scheduler.SchedulerProvider
import com.benitobertoli.liv.validator.Validator
import com.benitobertoli.liv.validator.ValidatorState
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.schedulers.TestScheduler
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LivTest {
    @get:Rule
    var rule: TestRule = RxSynchronizeSchedulersRule()

    private val testScheduler = TestScheduler()
    private var schedulerProvider = mock<SchedulerProvider> {
        on { backgroundScheduler }.thenReturn(testScheduler)
        on { foregroundScheduler }.thenReturn(testScheduler)
    }

    open class TestValidator : Validator() {
        override fun validate() {

        }

        override fun dispose() {
        }

    }

    private val firstValidator = TestValidator()
    private val secondValidator = TestValidator()

    private val action = mock<() -> Unit>()

    @Test
    fun `submit action is not called on initialization`() {
        val liv = Liv(schedulerProvider, listOf(firstValidator, secondValidator), action)
        liv.start()
        testScheduler.triggerActions()

        liv.submitWhenValid()

        verify(action, never()).invoke()
    }

    @Test
    fun `if one validator is invalid submit is not called`() {
        val liv = Liv(schedulerProvider, listOf(firstValidator, secondValidator), action)
        liv.start()
        testScheduler.triggerActions()

        liv.submitWhenValid()

        firstValidator.value = ValidatorState.VALID
        secondValidator.value = ValidatorState.INVALID
        testScheduler.triggerActions()

        verify(action, never()).invoke()
    }

    @Test
    fun `if one validator is not validated submit is not called`() {
        val liv = Liv(schedulerProvider, listOf(firstValidator, secondValidator), action)
        liv.start()
        testScheduler.triggerActions()

        liv.submitWhenValid()

        firstValidator.value = ValidatorState.VALID
        secondValidator.value = ValidatorState.NOT_VALIDATED
        testScheduler.triggerActions()

        verify(action, never()).invoke()
    }

    @Test
    fun `submitWhenValid called before emitting input`() {
        val liv = Liv(schedulerProvider, listOf(firstValidator, secondValidator), action)
        liv.start()
        testScheduler.triggerActions()

        liv.submitWhenValid()

        firstValidator.value = ValidatorState.VALID
        secondValidator.value = ValidatorState.VALID
        testScheduler.triggerActions()

        verify(action, times(1)).invoke()
    }

    @Test
    fun `submitWhenValid called after emitting input`() {
        val liv = Liv(schedulerProvider, listOf(firstValidator, secondValidator), action)
        liv.start()
        testScheduler.triggerActions()

        firstValidator.value = ValidatorState.VALID
        secondValidator.value = ValidatorState.VALID
        testScheduler.triggerActions()

        liv.submitWhenValid()

        verify(action, times(1)).invoke()
    }
}