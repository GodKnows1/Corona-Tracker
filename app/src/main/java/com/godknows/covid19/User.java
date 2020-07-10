package com.godknows.covid19;

public class User {
    private String email, status,uid;

    public String getStatus() {
        return status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public User(String email, String status,String uid) {
        this.email = email;
        this.status = status;
        this.uid=uid;

    }
}
