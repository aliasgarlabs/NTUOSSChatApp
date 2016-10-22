package com.aliasgarmurtaza.ntuosschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring views
    EditText etName, etPassword, etConfirmPassword, etEmail;
    Button bSignInOrUp;
    TextView bAlreadySignUp;
    TextInputLayout tILConfirmPassword;
    boolean signUpFeaturesVisible = false;

    //Declaring FirebaseAuth object
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing views
        etName = (EditText) findViewById(R.id.name);
        etPassword = (EditText) findViewById(R.id.password);
        etConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        etEmail = (EditText) findViewById(R.id.email);
        bSignInOrUp = (Button) findViewById(R.id.signInOrUp);
        bAlreadySignUp = (TextView) findViewById(R.id.alreadySignUp);
        tILConfirmPassword = (TextInputLayout) findViewById(R.id.confirmPasswordLayout);

        //Setting onClick Listeners
        bSignInOrUp.setOnClickListener(this);
        bAlreadySignUp.setOnClickListener(this);

        //Initializing FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alreadySignUp: {
                if (signUpFeaturesVisible) {
                    //Toggle - Hide Sign Up features
                    etName.setVisibility(View.GONE);
                    tILConfirmPassword.setVisibility(View.GONE);
                    bSignInOrUp.setText("SIGN IN");
                    bAlreadySignUp.setText("Sign up?");
                } else {
                    //Toggle - Show Sign Up features
                    etName.setVisibility(View.VISIBLE);
                    tILConfirmPassword.setVisibility(View.VISIBLE);
                    bSignInOrUp.setText("SIGN UP");
                    bAlreadySignUp.setText("Sign in?");
                }

                //Negate
                signUpFeaturesVisible = !signUpFeaturesVisible;

                break;
            }

            case R.id.signInOrUp: {
                signInOrSignUp();
                break;
            }
        }
    }

    private void signInOrSignUp() {

        String name, password, confirmPassword, email;

        name = etName.getText().toString();
        email = etName.getText().toString();
        password = etName.getText().toString();
        confirmPassword = etName.getText().toString();


        boolean validated = validate(name, email, password, confirmPassword);

        //TODO Dummy login - Should be removed after implementation
        if (validated || true) {
            //Proceed with Sign In or Sign UP
            if (signUpFeaturesVisible) {
                //Sign Up
                //TODO Add Sign Up functionality

                //TODO Dummy login - Should be removed after implementation
                openMessagesActivity();
            } else {
                //Sign In
                //TODO Add Sign In functionality

                //TODO Dummy login - Should be removed after implementation
                openMessagesActivity();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check your details", Toast.LENGTH_LONG).show();
        }

    }

    private void openMessagesActivity() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate(String name, String email, String password, String confirmPassword) {
        //Validating the input
        if (name.isEmpty())
            return false;
        if (email.isEmpty() && !email.contains("@"))
            return false;
        if (!password.equals(confirmPassword) || password.isEmpty())
            return false;
        return true;
    }
}
