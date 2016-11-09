package com.example.liv;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import com.benitobertoli.liv.Liv;
import com.benitobertoli.liv.MessageType;
import com.benitobertoli.liv.validator.EmailValidator;
import com.benitobertoli.liv.validator.LengthValidator;
import com.benitobertoli.liv.validator.RequiredValidator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.benitobertoli.liv.ValidationTime.LIVE;

public class MainActivity extends AppCompatActivity {

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
        liv = new Liv();

        RequiredValidator requiredValidator = new RequiredValidator();
        EmailValidator emailValidator = new EmailValidator();
        LengthValidator lengthValidator = new LengthValidator(8, 15);

        liv.add(requiredDuring, LIVE, requiredValidator);
        liv.add(requiredAfter, requiredValidator);

        liv.add(emailDuring, LIVE, emailValidator);
        liv.add(emailAfter, emailValidator);

        liv.add(lengthDuring, LIVE, lengthValidator);

        liv.add(emailRequiredLengthAfter, MessageType.MULTIPLE, emailValidator, requiredValidator, lengthValidator);
        // MessageType.SINGLE can also be used for multiple validators
    }

    @OnClick(R.id.submit)
    void onSubmitClick() {
        liv.validate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liv.onDestroy();
    }
}
