package com.aliasgarmurtaza.ntuosschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

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
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);

        email = getIntent().getStringExtra("email");

        if (savedInstanceState != null) {
            currentUser = (User) savedInstanceState.getSerializable("currentuser");
            email = savedInstanceState.getString("email");
            loadMessages();

        } else {
            getCurrentUserDetails(email);
            progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading messages...");
            progressDialog.show();
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

    private void getCurrentUserDetails(String email) {
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
        myMessageRef.child("messageType").setValue(Message.TYPE_TEXT);
        etMessageBox.setText("");

    }


    private void sendImageMessage(final String fromMessageText, Bitmap image) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String imageName = "IMG" + Calendar.getInstance().getTimeInMillis() + "_" + FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, 4);

        StorageReference storageRef = storage.getReferenceFromUrl("gs://ntuoss-chat.appspot.com");
        StorageReference spaceRef = storageRef.child("images/" + imageName + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        UploadTask uploadTask = spaceRef.putBytes(baos.toByteArray());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myMessageRef = database.getReference("messages").push();
                myMessageRef.child("from").setValue(fromMessageText);
                myMessageRef.child("imageURL").setValue(downloadUrl.toString());
                myMessageRef.child("messageType").setValue(Message.TYPE_IMAGE);
                etMessageBox.setText("");
            }
        });


    }

 /*

// Author: Aliasgar Murtaza
// This is a method to download images using Firebase methods. For demo purposes we are using glide
// as it solves the complexity of loading images asynchronously with recyclerview. Uncomment this method
// to use Firebase download methods.

 private void downloadImage(String imageURI)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(imageURI);


        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
     */


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

                    if (message.getFrom().equals(currentusername)) {
                        message.setFrom("You");
                    }


                    messages.add(message);
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                messagesRecyclerAdapter.notifyItemInserted(messages.size() - 1);
                messageRecyclerView.smoothScrollToPosition(messages.size() - 1);

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

                return super.onOptionsItemSelected(item);

        }
    }
}
