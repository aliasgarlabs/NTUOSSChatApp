package com.aliasgarmurtaza.ntuosschat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Aliasgar Murtaza on 17/10/16.
 */
public class MessagesActivity extends AppCompatActivity {

    //Declaring views
    TextView tvMessages;
    Button sendMessage;
    EditText etMessageBox;

    //Declaring variables
    String messages;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);

        tvMessages = (TextView) findViewById(R.id.messages);
        etMessageBox = (EditText) findViewById(R.id.et_message_box);
        sendMessage = (Button) findViewById(R.id.button_send);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newMessageText = etMessageBox.getText().toString();

                if (validate(newMessageText)) {
                    sendMessage(newMessageText);
                }

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

    private void sendMessage(String newMessageText) {

        //TODO Dummy message send. Should be replaced after implementation
        messages = tvMessages.getText().toString() + "\nYou: " + newMessageText;
        tvMessages.setText(messages);
        etMessageBox.setText("");

    }
}
