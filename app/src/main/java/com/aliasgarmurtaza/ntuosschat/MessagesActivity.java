package com.aliasgarmurtaza.ntuosschat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Aliasgar Murtaza on 17/10/16.
 */
public class MessagesActivity extends AppCompatActivity {

    //Declaring views
    Button sendMessage;
    EditText etMessageBox;
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

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO Dummy fromText String. Should be replaced after implementation
                String fromMessageText = "You";
                String newMessageText = etMessageBox.getText().toString();


                if (validate(newMessageText)) {
                    sendMessage(newMessageText, fromMessageText);
                }

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

    private void sendMessage(String newMessageText, String fromMessageText) {

        //TODO Dummy message send. Should be replaced after implementation
        Message message = new Message(fromMessageText, newMessageText);
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
}
