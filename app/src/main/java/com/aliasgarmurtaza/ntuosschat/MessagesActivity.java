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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);


        etMessageBox = (EditText) findViewById(R.id.et_message_box);
        sendMessage = (Button) findViewById(R.id.button_send);
        messageRecyclerView = (RecyclerView) findViewById(R.id.messagesRecylerView);
        takeImage = (Button) findViewById(R.id.button_add_picture);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO Dummy fromText String. Should be replaced after implementation
                String fromMessageText = "You";
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



        loadMessages();

    }

    private boolean validate(String newMessageText) {
        if (newMessageText.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendTextMessage(String fromMessageText, String newMessageText) {

        //TODO Dummy message send. Should be replaced after implementation
        Message message = new Message(fromMessageText, newMessageText);
        messages.add(message);
        etMessageBox.setText("");
        messageRecyclerView.invalidate();
    }


    private void sendImageMessage(String fromMessageText, Bitmap image) {

        //TODO Dummy message send. Should be replaced after implementation
        Message message = new Message(fromMessageText, image);
        messages.add(message);
        etMessageBox.setText("");
        messageRecyclerView.invalidate();
    }

    private void loadMessages() {
        messages.add(new Message("Ali", "Hello!"));
        messages.add(new Message("NTUOSS", "How's the workshop going on?"));
        messages.add(new Message("Shawn", "Firebase is awesome!"));

        MessagesRecyclerAdapter messagesRecyclerAdapter = new MessagesRecyclerAdapter(messages, getApplicationContext());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messageRecyclerView.setLayoutManager(mLayoutManager);
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messagesRecyclerAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //TODO Dummy fromText String. Should be replaced after implementation
            String fromMessageText = "You";
            sendImageMessage(fromMessageText, imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
