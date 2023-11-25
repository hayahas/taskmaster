package com.haya.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    String email;
    EditText loginEmail;
    EditText loginPassword;
    public final static String TAG = "LoginActivity";
    public static final String LOGIN_EMAIL_TAG = "Login_Email_Tag";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setupLoginBtn();
    }

    public void setupLoginBtn(){
        Intent callingIntent = getIntent();
        email= callingIntent.getStringExtra(VerifyAccountActivity.VERIFICATION_EMAIL_TAG);
        loginEmail = (EditText) findViewById(R.id.emailEditTextLoginPage);
        loginEmail.setText(email);

        loginPassword = (EditText) findViewById(R.id.passwordEditTextLoginPage);

        loginBtn = (Button) findViewById(R.id.loginBtnLoginPage);


        loginBtn.setOnClickListener(v -> {

            String userEmail = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            Amplify.Auth.signIn(userEmail,password,
                    success -> {
                        Log.i("TAG", "LoginActivity() : Logged in successfully" + success.toString());
                        Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(goToMain);
                    },
                    failure -> {
                        Log.i("TAG", "LoginActivity() : Failed to Log in" + failure.toString());
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Incorrect Email or Password" , Toast.LENGTH_LONG);
                        });
                    }
            );
        });
    }

}