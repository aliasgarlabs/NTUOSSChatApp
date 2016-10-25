package com.aliasgarmurtaza.ntuosschat;

import android.graphics.Bitmap;

/**
 * Created by Aliasgar Murtaza on 17/10/16.
 * This is a model of Message object.
 */
public class Message {


    public static int TYPE_TEXT = 0;
    public static int TYPE_IMAGE = 1;

    //Message properties. This is the most basic message object for simple demo purpose.
    //In real life, we would have timestamp, receiver, etc...

    String text;
    String from;
    Bitmap image;
    int messageType;
    String imageURL;

    public Message() {
        //We need an empty constructor for Firebase.
        // This constructor allows Firebase to initialize objects by itself
        // without us having to  manually assigning all attributes.
    }

    public Message(String from, String text) {
        this.text = text;
        this.from = from;
        this.messageType = TYPE_TEXT;
    }

    public Message(String from, Bitmap image) {
        this.image = image;
        this.from = from;
        this.messageType = TYPE_IMAGE;
    }

    public Message(String from, Bitmap image, String imageURL) {
        this.from = from;
        this.image = image;
        this.imageURL = imageURL;
    }

    //Getters and setter. Nothing interesting here.
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getMessageType() {
        return messageType;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
