package com.aliasgarmurtaza.ntuosschat;

import java.io.Serializable;

/**
 * Created by Aliasgar Murtaza on 24/10/16.
 * User model here.
 */
public class User implements Serializable {

    //Very primitive attributes here just for the sake of simplicity.
    String name;
    String email;

    public User()
    {
        //We need an empty constructor for Firebase.
        // This constructor allows Firebase to initialize objects by itself
        // without us having to  manually assigning all attributes.
    }

    public User(String name, String email)
    {
        this.name = name;
        this.email = email;
    }

    //Setters and getter here.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
