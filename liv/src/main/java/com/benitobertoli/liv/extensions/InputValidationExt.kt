@file:JvmName("LivUtilsUtils")

package com.benitobertoli.liv.extensions

import com.benitobertoli.liv.rule.Rule
import com.benitobertoli.liv.scheduler.SchedulerProvider
import com.benitobertoli.liv.scheduler.SchedulerProviderImpl
import com.benitobertoli.liv.validator.FieldValidator
import com.benitobertoli.liv.validator.MessageType
import com.benitobertoli.liv.validator.TextInputLayoutField
import com.benitobertoli.liv.validator.ValidationTime
import com.google.android.material.textfield.TextInputLayout


@JvmName("textInputLayoutValidator")
@JvmOverloads
fun TextInputLayout.validator(
        rules: List<Rule>,
        time: ValidationTime = ValidationTime.LIVE,
        messageType: MessageType = MessageType.SINGLE,
        schedulerProvider: SchedulerProvider = SchedulerProviderImpl
): FieldValidator {
    return FieldValidator(schedulerProvider, TextInputLayoutField(this), rules, time, messageType)
}