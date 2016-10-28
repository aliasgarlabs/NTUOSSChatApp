# NTUOSS Chat App
**Welcome to NTUOSSHack #57!** In order to make this workshop valuable for all participants with varying pace and expertise, the workshop is divided into 4 milestones.
The `Milestone 0` is the starter code which you can clone to your working directly. We will then move step by step towards `Milestone 1` and further. 

In the process, if you're lost or have encountered errors, you have the flexibility to jump to any milestone you wish by just checking out to the milestone branch of your choice so that all of us are on the same page. 

######Have a great evening!

##Milestone 1: Authentication 


###1. Declare FirebaseAuth object

*We need to declare FirebaseAuth object which is responsible for all authentication work with our app.*


```
//Declaring FirebaseAuth object
private FirebaseAuth mAuth;
```
>Copy this code to MainActivity.java file just before onCreate() such that it's accessible throughout the class.


###2. Initialize Firebase Auth

*This initializes the FirebaseAuth object we created earlier.*

```
    private void initFirebaseAuth() {
        //Initializing FirebaseAuth.
        //This object is responsible for all sign in, sign up and sign out.
        mAuth = FirebaseAuth.getInstance();
    }
```
>Add  `initFirebaseAuth()` in `MainActivity.java`


###3. Add createUser() Functionality

```
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
```

>Replace `createUserDummy()` with this method in 'MainActivity.java`


###4. Add SignInUser() Functionality

```
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
```

>Replace `signInUserDummy()` with this method in `MainActivity.java`


###5. Check If User Is Logged In

*Retreive the login status of the user after the app is restarted.*

```
    private boolean checkIfUserIsLoggedIn() {
        //Checking for already logged in user is this simple. No more maintaining of tokens and stuff.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            return true;
        return false;
    }
```

>Replace `checkIfUserIsLoggedInDummy()` with `checkIfUserIsLoggedIn()` in `MainActivity.java`

###6. Add getCurrentUser() Functionality

```
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
                    //TODO replace fetchMessageDummy() with actual implementation in Milestone 2
                    fetchMessagesDummy();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle cancellations here
            }
        });
    }
```

>Replace `getCurrentUserDetailsDummy()`. This is not part of this milestone but we need this for complete createUser process flow. Just copy the code and we will discuss it in the next milestone.

###7. Sign Out Functionality

*You'll love signing out because it's so easy! Just one line of code takes care of all your tokens, authentication objects and every other parameter!*

```
FirebaseAuth.getInstance().signOut();
```

>Add this in the TODO which you'll find in switch case inside `onOptionsItemSelected()` in `MessageActivity.java.` 


Congratulations, you completed the Milestone 1 :)



##Milestone 2: Database

###1. Fetch Messages From DB

```
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

```

>Replace `fetchMessageDB()` with this method in `MessageActivity.java`


####2. Add Messages To DB

```
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
```

>Replace `addMessageDummy()` with this method in `MessageActivity.java`

####3. Show Progress Bar

`showProgressDialog(); `

>Unncomment the call to `showProgressDialog()` as we are now making a network call. Well, actually Firebase is making the call for us ;)


Congratulations, you completed the Milestone 2 :)



##Milestone 3: Storage

###1. Upload Images

```
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
```

>Replace `uploadImageDummy()` with this method.  


###2. Download Images

`Glide.with(context).load(message.getImageURL()).into(holder.imageView);`

>Uncomment this line in `MessagesRecyclerAdapter.java` to download images with Glide.


**Bonus Download method**

```
    //                                      ****BONUS*****
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
 ```
 
>We are using Glide to download pictures as it's amazing for dynamic lists like `RecyclerView` which we are using. If you did like to download using FirebaseAPI, you can use this code. 
>Note: This method can download any kind of file, but Glide is only for images. 
 
###Bravo! You made it! Congratulations on having your own real-time chat app on Android. 
 
Please do let me know about your cool projects you do with Firebase. Would love to check them out! :)

Hope you enjoyed! 
