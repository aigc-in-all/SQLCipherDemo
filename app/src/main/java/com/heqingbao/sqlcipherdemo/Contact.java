package com.heqingbao.sqlcipherdemo;

public class Contact {

    private int mId;
    private String mName;
    private String mPhoneNumber;

    public Contact() {

    }

    public Contact(int id, String name, String phoneNumber) {
        mId = id;
        mName = name;
        mPhoneNumber = phoneNumber;
    }

    public Contact(String name, String _phone_number) {
        mName = name;
        mPhoneNumber = _phone_number;
    }

    public int getID() {
        return mId;
    }

    public void setID(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phone_number) {
        this.mPhoneNumber = phone_number;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                '}';
    }
}