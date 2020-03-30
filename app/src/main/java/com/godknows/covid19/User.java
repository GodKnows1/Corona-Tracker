package com.godknows.covid19;

public class User {
    private String email, status;

    public String getStatus() {
        return status;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public User(String email, String status) {
        this.email = email;
        this.status = status;
    }
}
