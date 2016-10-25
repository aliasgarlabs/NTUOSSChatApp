package com.aliasgarmurtaza.ntuosschat;

import java.io.Serializable;

/**
 * Created by mac on 24/10/16.
 */
public class User implements Serializable {

    String name;
    String email;

    public User()
    {

    }

    public User(String name, String email)
    {
        this.name = name;
        this.email = email;
    }
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
