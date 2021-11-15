package com.lockpad.sslockscreen.security.livedata;

import androidx.lifecycle.LiveData;

public class LPLiveData<T> extends LiveData<T> {

    public void setData(T data) {
        setValue(data);
    }

}
