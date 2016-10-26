package com.aliasgarmurtaza.ntuosschat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class MessagesActivity extends AppCompatActivity implements View.OnClickListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Declaring views
    private Button sendMessage, takeImage;
    private EditText etMessageBox;
    private RecyclerView messageRecyclerView;

    //Declaring variables
    private ArrayList<Message> messages = new ArrayList<>();
    private User currentUser;
    private ProgressDialog progressDialog;
    private MessagesRecyclerAdapter messagesRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);

        initializeViews();
        showProgressDialog();
        getCurrentUserDetailsFromDB();

    }


    private void initializeViews()
    {
        etMessageBox = (EditText) findViewById(R.id.et_message_box);
        sendMessage = (Button) findViewById(R.id.button_send);
        messageRecyclerView = (RecyclerView) findViewById(R.id.messagesRecylerView);
        takeImage = (Button) findViewById(R.id.button_add_picture);
        progressDialog = new ProgressDialog(MessagesActivity.this);

        //Setting onClick listeners
        sendMessage.setOnClickListener(this);
        takeImage.setOnClickListener(this);

        //Initializing recyclerView elements
        messagesRecyclerAdapter = new MessagesRecyclerAdapter(messages, getApplicationContext());
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messageRecyclerView.setLayoutManager(mLayoutManager);
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messagesRecyclerAdapter);
    }

    private void showProgressDialog() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading messages...");
        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                //Time out
                if(progressDialog.isShowing())
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 5000);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getApplicationContext(),"Time out. Seems like no message on DB.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Retrieving the thumbnail for sake of simplicity. Should retrieve complete image ideally.
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadImageToStorage(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        //Method to start camera to take picture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onClick(View view) {
        //OnClick listener to listen to submit and camera buttons
        switch (view.getId()) {
            case R.id.button_send:
            {
                String textMessage = etMessageBox.getText().toString();

                //Validating message
                if (!textMessage.isEmpty())
                    addMessageToDB(Message.TYPE_TEXT, textMessage);
                else
                    Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.button_add_picture:
            {
                dispatchTakePictureIntent();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating the actionbar menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //To listen to sign out button click on Action Bar
        switch (item.getItemId()) {
            case R.id.action_signout:
                //Code to sign out. That simple!
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCurrentUserDetailsFromDB() {
        // This method's purpose is to demonstrate how a query in Firebase looks like.
        // Gone are the days where we used SELECT commands.
        // This method's purpose is to find the name of the current user.
        // Again, remember Firebase is a NO-SQL database, It has tree like structure.
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

        //First we are going to the root and then to the child called users.
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");

        //Now we want to search users by email. Here's the query for that.
        Query query = userReference.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //Now we are executing the search!
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // A snapshot is a representations of the data from the database
                // We can traverse through multiple results (children of snapshots) like this.
                for (DataSnapshot messageChild : dataSnapshot.getChildren()) {
                    // You and I know that each user will have unique email
                    // and that's why there will be only 1 child.
                    // We assign the child to our User model like this.
                    // Remember the variable names in the model class should
                    // match with the attributes in the database.
                    currentUser = messageChild.getValue(User.class);

                    // Fetching messages now. The reason it is placed here is because
                    // we want synchronization
                    fetchMessagesFromDB();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle cancellations here
            }
        });
    }

    private void addMessageToDB(int messageType, String messageContent) {

        //Creating a messages reference and then adding the children attributes
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myMessageRef = database.getReference("messages").push(); //Push means appending
        myMessageRef.child("from").setValue(currentUser.getName());
        myMessageRef.child("messageType").setValue(messageType);

        //Checking if we should add text or imageURL based on messageType
        if(messageType == Message.TYPE_TEXT)
            myMessageRef.child("text").setValue(messageContent);
        else if(messageType == Message.TYPE_IMAGE)
            myMessageRef.child("imageURL").setValue(messageContent);

        //Resetting the editText field
        etMessageBox.setText("");
    }


    private void uploadImageToStorage(Bitmap image) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Creating a unique image name with combination of time and first 4 letters of the email address
        String imageName = "IMG" + Calendar.getInstance().getTimeInMillis() + "_" + FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, 4);

        //Storage reference of the bucket on the database. Consider bucket as your drive on Google Drive.
        StorageReference storageRef = storage.getReferenceFromUrl("gs://ntuoss-chat.appspot.com"); //Bucket id for NTU OSS app

        //Storage reference of our new image. Basically, this is the path.
        StorageReference imageRef = storageRef.child("images/" + imageName + ".jpg");

        //Transforming bitmap to bytes[] for upload
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        //Uploads are done using uploadTask
        UploadTask uploadTask = imageRef.putBytes(byteArrayOutputStream.toByteArray());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                //URL for the image. We need to save it in the message object and upload the message in the DB now.
                // Remember, storage and DB are different things on Firebase.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                addMessageToDB(Message.TYPE_IMAGE, downloadUrl.toString());
            }
        });


    }

    private void fetchMessagesFromDB() {

        //Creating reference of messages and then adding a listener.
        //Anytime the database is changed, this listener will be called.
        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference("messages");
        messagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clearing existing messages to avoid duplicates
                messages.clear();

                //Iterating through the children (messages)
                for (DataSnapshot messageChild : dataSnapshot.getChildren()) {
                    //Initializing message objects using Firebase
                    Message message = messageChild.getValue(Message.class);

                    //If the message was sent by current user, replace name. Just for demo. In real-life, should check userID.
                    if (message.getFrom().equals(currentUser.getName())) {
                        message.setFrom("You");
                    }

                    //Add messages to messages arrayList
                    messages.add(message);
                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                //Notifying recyclerViewAdapter about new messages
                if(messages.size()==1)
                {
                    messageRecyclerView.smoothScrollToPosition(messages.size());
                    messagesRecyclerAdapter.notifyDataSetChanged();
                }
                else if(messages.size()>1)
                {
                    messageRecyclerView.smoothScrollToPosition(messages.size() - 1);
                    messagesRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle cancellations here
            }
        });
    }

    /*
                                            ****BONUS*****
    // Author: Aliasgar Murtaza
    // This is a method to download images using Firebase methods. For demo purposes we are using glide
    // as it solves the complexity of loading images asynchronously with recyclerView. Uncomment this method
    // to use Firebase download methods.

     private void downloadImage(String imageURI)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageURI);

            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
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
}
