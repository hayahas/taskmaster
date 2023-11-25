package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

public class SignupActivity extends AppCompatActivity {

    Button signupBtn;
    public final static String TAG = "SignupActivity";
    public static final String SIGNUP_EMAIL_TAG = "Signup_Email_Tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        setupSignupAuth();
    }

    private void setupSignupAuth(){

        signupBtn = (Button) findViewById(R.id.signupBtnSignPage);

        signupBtn.setOnClickListener(v -> {

            String email = ((EditText)findViewById(R.id.emailEditTextSignupPage)).getText().toString();
            String nickname = ((EditText)findViewById(R.id.usernameEditTextSignup)).getText().toString();
            String password = ((EditText)findViewById(R.id.passwordEditTextSignupPage)).getText().toString();


            Amplify.Auth.signUp(email,
                    password,
                    AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(),email)
                    .userAttribute(AuthUserAttributeKey.nickname(),nickname)
                     .build(),
                    success -> {
                Log.i(TAG, "SignupActivity() : Signed up successfully" + success.toString());
                        Intent goToVerifyIntent= new Intent(SignupActivity.this, VerifyAccountActivity.class);
                        goToVerifyIntent.putExtra(SIGNUP_EMAIL_TAG, email);
                        startActivity(goToVerifyIntent);
                    },
                    failure -> {
                        Log.i(TAG, "SignupActivity() : Failed to Sign up " + failure.toString());
                        runOnUiThread( () -> {
                                Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_LONG).show();
                        } );
                    }
                    );


        });

    }

}