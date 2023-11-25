package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;

public class VerifyAccountActivity extends AppCompatActivity {

    Button verifyBtn;

    public final static String TAG = "VerifyAccountActivity";
    public static final String VERIFICATION_EMAIL_TAG = "Verification_Email_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_account);

        setupVerification();
    }

    public void setupVerification(){

        Intent callingIntent = getIntent();
        String email = callingIntent.getStringExtra(SignupActivity.SIGNUP_EMAIL_TAG);
        EditText verifyAccEmail = (EditText) findViewById(R.id.emailEditTextVerifyPage);
        verifyAccEmail.setText(email);

        verifyBtn = (Button) findViewById(R.id.verifyBtnVerifyPage);

        verifyBtn.setOnClickListener(v ->{

            String userEmail= verifyAccEmail.getText().toString();

            String verifyCode = ((EditText) findViewById(R.id.codeEditTextVerifyPage)).getText().toString() ;

            Amplify.Auth.confirmSignUp(userEmail,verifyCode,
                    success -> {
                        Log.i( TAG, "VerifyAccountActivity() : Verified Successfully " + success.toString());
                        Intent goToLogin = new Intent(VerifyAccountActivity.this, LoginActivity.class);
                        goToLogin.putExtra(VERIFICATION_EMAIL_TAG,userEmail);
                        startActivity(goToLogin);
                    },
                    failure -> {
                        Log.i( TAG, "VerifyAccountActivity() : Failed to verify account " + failure.toString());
                        runOnUiThread(()->{
                            Toast.makeText(VerifyAccountActivity.this, "Incorrect Code " , Toast.LENGTH_LONG).show();
                        });
                    });

        });

    }
}