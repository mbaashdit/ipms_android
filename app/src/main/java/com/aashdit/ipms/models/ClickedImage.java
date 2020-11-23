package com.aashdit.ipms.models;

import android.graphics.Bitmap;

public class ClickedImage {
    private Bitmap imageCaptured;

    public ClickedImage(Bitmap imageCaptured) {
        this.imageCaptured = imageCaptured;
    }

    public Bitmap getImageCaptured() {
        return imageCaptured;
    }

    public void setImageCaptured(Bitmap imageCaptured) {
        this.imageCaptured = imageCaptured;
    }
}
