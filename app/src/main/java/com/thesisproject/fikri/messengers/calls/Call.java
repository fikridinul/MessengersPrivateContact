package com.thesisproject.fikri.messengers.calls;

import android.net.Uri;

public class Call {

    private String _name, _phone, _time, _long;
    private Uri _imageUri;
    private int _id;

    public Call (int id, String name, String phone, String time, String longs, Uri imageUri) {
        _id = id;
        _name = name;
        _phone = phone;
        _time = time;
        _long = longs;
        _imageUri = imageUri;
    }

    public int getId() {
        return _id;
    }

    public Uri getImageUri() {
        return _imageUri;
    }

    public String getLong() {
        return _long;
    }

    public String getName() {
        return _name;
    }

    public String getPhone() {
        return _phone;
    }

    public String getTime() {
        return _time;
    }
}
