package com.aliasgarmurtaza.ntuosschat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Aliasgar Murtaza on 17/10/16.
 */
public class MessagesActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    //Declaring views
    Button sendMessage, takeImage;
    EditText etMessageBox;
    ImageView ivImage;
    RecyclerView messageRecyclerView;
    //Declaring variables
    ArrayList<Message> messages = new ArrayList<>();
    String email;
    User currentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);

        email = getIntent().getStringExtra("email");

        if(savedInstanceState!=null)
        {
            currentUser = (User) savedInstanceState.getSerializable("currentuser");
            email =  savedInstanceState.getString("email");
            loadMessages();

        }
        else {

            getCurrentUserDetails(email);
        }



        etMessageBox = (EditText) findViewById(R.id.et_message_box);
        sendMessage = (Button) findViewById(R.id.button_send);
        messageRecyclerView = (RecyclerView) findViewById(R.id.messagesRecylerView);
        takeImage = (Button) findViewById(R.id.button_add_picture);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String fromMessageText = currentUser.getName();

                String newMessageText = etMessageBox.getText().toString();


                if (validate(newMessageText)) {
                    sendTextMessage(fromMessageText, newMessageText);
                }

            }
        });

        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });




    }

    private boolean validate(String newMessageText) {
        if (newMessageText.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getCurrentUserDetails(String email)
    {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = userReference.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageChild : dataSnapshot.getChildren()) {
                     currentUser = messageChild.getValue(User.class);
                    loadMessages();
                    Log.d("TAG", dataSnapshot.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendTextMessage(String fromMessageText, String newMessageText) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myMessageRef = database.getReference("messages").push();
        myMessageRef.child("from").setValue(fromMessageText);
        myMessageRef.child("text").setValue(newMessageText);




        etMessageBox.setText("");

    }


    private void sendImageMessage(String fromMessageText, Bitmap image) {

        //TODO Dummy message send. Should be replaced after implementation
        Message message = new Message(fromMessageText, image);
        messages.add(message);
        etMessageBox.setText("");
        messageRecyclerView.invalidate();
    }

    private void loadMessages() {


        final MessagesRecyclerAdapter messagesRecyclerAdapter = new MessagesRecyclerAdapter(messages, getApplicationContext());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messageRecyclerView.setLayoutManager(mLayoutManager);
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messagesRecyclerAdapter);


        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference("messages");
        messagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot messageChild : dataSnapshot.getChildren()) {
                    Message message = messageChild.getValue(Message.class);

                    String currentusername = currentUser.getName();

                    if(message.getFrom().equals(currentusername))
                    {
                        message.setFrom("You");
                    }


                    messages.add(message);
                }

               messagesRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String fromMessageText = currentUser.getName();
            sendImageMessage(fromMessageText, imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentuser", currentUser);
        outState.putSerializable("email", email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_signout:
                    FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
