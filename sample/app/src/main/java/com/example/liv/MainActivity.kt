package com.example.liv

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.benitobertoli.liv.Liv
import com.benitobertoli.liv.extensions.validator
import com.benitobertoli.liv.rule.EmailRule
import com.benitobertoli.liv.rule.MaxLengthRule
import com.benitobertoli.liv.rule.MinLengthRule
import com.benitobertoli.liv.rule.NotBlankRule
import com.benitobertoli.liv.validator.ValidationTime.AFTER
import com.benitobertoli.liv.validator.ValidatorState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private var liv: Liv? = null

    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        submitButton = findViewById(R.id.submit)
        val requiredDuring = findViewById<TextInputLayout>(R.id.required_during_layout)
        val requiredAfter = findViewById<TextInputLayout>(R.id.required_after_layout)
        val emailDuring = findViewById<TextInputLayout>(R.id.email_during_layout)
        val emailAfter = findViewById<TextInputLayout>(R.id.email_after_layout)
        val lengthDuring = findViewById<TextInputLayout>(R.id.length_during_layout)
        val emailRequiredLengthDuring = findViewById<TextInputLayout>(R.id.email_required_length_during_layout)

        submitButton.setOnClickListener { liv?.submitWhenValid() }

        val notBlankRule = NotBlankRule("Required")
        val emailRule = EmailRule("Invalid email address")
        val minLengthRule = MinLengthRule("Min 8 characters", 8)
        val maxLengthRule = MaxLengthRule("Max 15 characters", 15)

        liv = Liv(
                validators = listOf(
                        requiredDuring.validator(listOf(notBlankRule)),
                        requiredAfter.validator(listOf(notBlankRule), AFTER),
                        emailDuring.validator(listOf(emailRule)),
                        emailAfter.validator(listOf(emailRule), AFTER),
                        lengthDuring.validator(listOf(minLengthRule, maxLengthRule)),
                        emailRequiredLengthDuring.validator(listOf(notBlankRule, emailRule, minLengthRule, maxLengthRule))
                ),
                stateChanges = { validatorState -> setButtonEnabled(validatorState) },
                submitAction = { performAction() }
        )
        liv?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        liv?.dispose()
    }

    private fun setButtonEnabled(validatorState: ValidatorState) {
        submitButton.isEnabled = validatorState == ValidatorState.VALID
    }

    private fun performAction() {
        Snackbar.make(submitButton, "Valid. Submitting...", Snackbar.LENGTH_SHORT).show()
    }
}
