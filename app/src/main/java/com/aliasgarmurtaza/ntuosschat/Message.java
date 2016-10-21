package com.aliasgarmurtaza.ntuosschat;

import android.graphics.Bitmap;

/**
 * Created by mac on 17/10/16.
 */
public class Message {

    public static int TYPE_TEXT = 0;
    public static int TYPE_IMAGE = 1;
    String message;
    String from;
    Bitmap image;
    int messageType;

    public Message(String from, String message) {
        this.message = message;
        this.from = from;
        this.messageType = TYPE_TEXT;
    }


    public Message(String from, Bitmap image) {
        this.image = image;
        this.from = from;
        this.messageType = TYPE_IMAGE;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
