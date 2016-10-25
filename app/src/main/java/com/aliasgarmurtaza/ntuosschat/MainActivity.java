package com.aliasgarmurtaza.ntuosschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring views
    EditText etName, etPassword, etConfirmPassword, etEmail;
    Button bSignInOrUp;
    TextView bAlreadySignUp;
    TextInputLayout tILConfirmPassword;
    boolean signUpFeaturesVisible = false;

    //Declaring FirebaseAuth object
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String TAG = "MainActivity";
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
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        if(checkIfUserIsLoggedIn())
        {
            openMessagesActivity(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }


    }

    private boolean checkIfUserIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return true;
        }
        return false;
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signInOrSignUp() {

        String name, password, confirmPassword, email;

        name = etName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();


        boolean validated = validate(name, email, password, confirmPassword);

        //TODO Dummy login - Should be removed after implementation
        if (validated || true) {
            //Proceed with Sign In or Sign UP
            if (signUpFeaturesVisible) {
                //Sign Up
             createUser(name, email,password);

            } else {
                //Sign In
             signInUser(email,password);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check your details", Toast.LENGTH_LONG).show();
        }

    }

    private void openMessagesActivity(String email) {
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra("email", email);
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

    private void createUser(final String name, final String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sign up failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            updateUserInDB(task.getResult().getUser().getUid(), name, email);
                            openMessagesActivity(email);
                        }
                    }
                });
    }

    private void signInUser(final String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            openMessagesActivity(email);
                        }

                    }
                });
    }

    private void updateUserInDB(String uid, String name, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+uid);
        myRef.child("name").setValue(name);
        myRef.child("email").setValue(email);
    }
}
