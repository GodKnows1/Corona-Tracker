package com.godknows.covid19;

class Register {

    private String email,name,phone,uid;

    public Register(){

    }

    public Register(String email, String uid,String name,String phone) {
        this.email=email;
        this.uid=uid;
        this.name=name;
        this.phone=phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
