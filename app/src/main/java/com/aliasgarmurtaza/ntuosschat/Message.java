package com.aliasgarmurtaza.ntuosschat;

import android.graphics.Bitmap;

/**
 * Created by Aliasgar Murtaza on 17/10/16.
 */
public class Message {

    public static int TYPE_TEXT = 0;
    public static int TYPE_IMAGE = 1;
    String text;
    String from;
    Bitmap image;
    int messageType;

    public Message()
    {

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
