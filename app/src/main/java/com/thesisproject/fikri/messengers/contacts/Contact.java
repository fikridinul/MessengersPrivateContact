package com.thesisproject.fikri.messengers.contacts;

import android.net.Uri;

public class Contact {

    private String _name, _phone;
    private Uri _imageURI;
    private int _id;

    public Contact (int id, String name, String phone, Uri imageURI) {
        _id = id;
        _name = name;
        _phone = phone;
        _imageURI = imageURI;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getPhone() {
        return _phone;
    }

    public Uri getImageURI() {
        return _imageURI;
    }
}
