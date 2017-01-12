package com.example.liv;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.benitobertoli.liv.Liv;
import com.benitobertoli.liv.MessageType;
import com.benitobertoli.liv.ValidatorState;
import com.benitobertoli.liv.rule.EmailRule;
import com.benitobertoli.liv.rule.LengthRule;
import com.benitobertoli.liv.rule.NotEmptyRule;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.benitobertoli.liv.ValidationTime.LIVE;

public class MainActivity extends AppCompatActivity implements Liv.Callback, Liv.Action {

    @BindView(R.id.required_during_layout) TextInputLayout requiredDuring;
    @BindView(R.id.required_after_layout) TextInputLayout requiredAfter;
    @BindView(R.id.email_during_layout) TextInputLayout emailDuring;
    @BindView(R.id.email_after_layout) TextInputLayout emailAfter;
    @BindView(R.id.length_during_layout) TextInputLayout lengthDuring;
    @BindView(R.id.email_required_length_after_layout) TextInputLayout emailRequiredLengthAfter;

    private Liv liv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        NotEmptyRule notEmptyRule = new NotEmptyRule();
        EmailRule emailRule = new EmailRule();
        LengthRule lengthRule = new LengthRule(8, 15);

        liv = new Liv.Builder().add(requiredDuring, LIVE, notEmptyRule)
                .add(requiredAfter, notEmptyRule)
                .add(emailDuring, LIVE, emailRule)
                .add(emailAfter, emailRule)
                .add(lengthDuring, LIVE, lengthRule)
                .add(emailRequiredLengthAfter, MessageType.MULTIPLE, emailRule, notEmptyRule, lengthRule)
                // Note: MessageType.SINGLE can also be used for multiple validators
                .callback(this) // only needed if you need more control over form validation
                .submitAction(this)
                .build();
        liv.start();
    }

    @OnClick(R.id.submit)
    void onSubmitClick() {
        liv.submitWhenValid();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liv.onDestroy();
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
