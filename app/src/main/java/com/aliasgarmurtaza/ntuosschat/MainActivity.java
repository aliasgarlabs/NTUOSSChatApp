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

/**
 * Created by Aliasgar Murtaza on 17/10/16.
 * This is activity handles login and signUp flow
 * and leads to MessageActivity.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MainActivity";

    //Declaring views
    private EditText etName, etPassword, etConfirmPassword, etEmail;
    private Button bSignInOrUp;
    private TextView bAlreadySignUp;
    private TextInputLayout tILConfirmPassword;
    private boolean signUpFeaturesVisible = false;

    //Declaring FirebaseAuth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initFirebaseAuth();

        if (checkIfUserIsLoggedIn()) {
            openMessagesActivity();
        }
    }

    private void initializeViews() {
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

                //Negate as state as changed
                signUpFeaturesVisible = !signUpFeaturesVisible;
                break;
            }

            case R.id.signInOrUp: {
                signInOrSignUp();
                break;
            }
        }
    }

    private boolean validate(String name, String email, String password, String confirmPassword) {
        //Validating the input
        if(email.isEmpty() || password.isEmpty())
            return false;

        if(signUpFeaturesVisible)
        {
            if(name.isEmpty()|| !password.equals(confirmPassword))
                return false;
        }
        return true;

    }

    private void signInOrSignUp() {
        //Sign in or sign up based on the views visible
        String name, password, confirmPassword, email;

        //Getting the EditText values
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();

        //Checking if input is valid
        boolean validated = validate(name, email, password, confirmPassword);

        if (validated) {
            //Proceed with Sign In or Sign UP
            if (signUpFeaturesVisible) {
                //Sign Up as sign up views are visible
                createUser(name, email, password);
            } else {
                //Sign In
                signInUser(email, password);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please check your details", Toast.LENGTH_LONG).show();
        }
    }

    private void openMessagesActivity() {
        //Launch the messaging activity
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private void createUser(final String name, final String email, String password) {
        //We are creating a new user here. All security measures are handled by Firebase.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sign up failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Now we want to add this user data in our DB so that we can store more attributes of the user
                            //apart from name and email.
                            updateUserInDB(task.getResult().getUser().getUid(), name, email);
                            openMessagesActivity();
                        }
                    }
                });
    }

    private void signInUser(String email, String password) {
        //Again, security is taken care of. Returns passwords wrong or user doesn't exists exceptions.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Sign in failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            openMessagesActivity();
                        }
                    }
                });
    }

    private void updateUserInDB(String uid, String name, String email) {
        //This is how we make a entry in the database. Yes, it's this simple.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + uid);

        //We made a object with name uid and we are adding two children to it.
        //The Firebase database with 2 users will look like this.
        // Remember Firebase is a NO-SQL database, It has tree like structure.
        // ------DB
        //        |
        //        |______users
        //                   |
        //                   |______s27t7y2s7w78h8sh                                //(RANDOM UID)
        //                   |                     |
        //                   |                     |______name = user1name
        //                   |                     |
        //                   |                     |______email = user1email
        //                   |
        //                   |______j9dj3h37673ey83d                                //(RANDOM UID)
        //                                         |
        //                                         |______name = user2name
        //                                         |
        //                                         |______email = user2email

        //Ofc we can add as many attributes we want.
        myRef.child("name").setValue(name);
        myRef.child("email").setValue(email);
    }

    private void initFirebaseAuth() {
        //Initializing FirebaseAuth.
        //This object is responsible for all sign in, sign up and sign out.
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean checkIfUserIsLoggedIn() {
        //Checking for already logged in user is this simple. No more maintaining of tokens and stuff.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            return true;
        return false;
    }

}
