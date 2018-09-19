package com.example.liv;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.benitobertoli.liv.Liv;
import com.benitobertoli.liv.rule.EmailRule;
import com.benitobertoli.liv.rule.LengthRule;
import com.benitobertoli.liv.rule.NotEmptyRule;
import com.benitobertoli.liv.validator.MessageType;
import com.benitobertoli.liv.validator.ValidatorState;

import static com.benitobertoli.liv.validator.ValidationTime.AFTER;
import static com.benitobertoli.liv.validator.ValidationTime.LIVE;

public class MainActivity extends AppCompatActivity implements Liv.Callback, Liv.Action {

    private TextInputLayout requiredDuring;

    private Liv liv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requiredDuring = findViewById(R.id.required_during_layout);
        TextInputLayout requiredAfter = findViewById(R.id.required_after_layout);
        TextInputLayout emailDuring = findViewById(R.id.email_during_layout);
        TextInputLayout emailAfter = findViewById(R.id.email_after_layout);
        TextInputLayout lengthDuring = findViewById(R.id.length_during_layout);
        TextInputLayout emailRequiredLengthAfter = findViewById(R.id.email_required_length_after_layout);
        findViewById(R.id.submit).setOnClickListener(view -> liv.submitWhenValid());

        NotEmptyRule notEmptyRule = new NotEmptyRule();
        EmailRule emailRule = new EmailRule();
        LengthRule lengthRule = new LengthRule(8, 15);

        liv = new Liv.Builder().add(requiredDuring, notEmptyRule)
                .add(requiredAfter, AFTER,notEmptyRule)
                .add(emailDuring, emailRule)
                .add(emailAfter, AFTER, emailRule)
                .add(lengthDuring, lengthRule)
                .add(emailRequiredLengthAfter, AFTER, MessageType.MULTIPLE, emailRule, notEmptyRule, lengthRule)
                // Note: MessageType.SINGLE can also be used for multiple validators
                .callback(this) // only needed if you need more control over form validation
                .submitAction(this)
                .build();
        liv.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liv.dispose();
    }

    @Override
    public void onStateChange(ValidatorState state) {
        Log.d("State Change", state.toString());
    }

    @Override
    public void performAction() {
        Snackbar.make(requiredDuring, "Valid. Submitting...", Snackbar.LENGTH_SHORT).show();
    }
}
