package com.angik.duodevloopers.food.Model;

import android.arch.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private String scannedId;

    public String getScannedId() {
        return scannedId;
    }

    public void setScannedId(String scannedId) {
        this.scannedId = scannedId;
    }
}
