package com.jaylax.wiredshack.eventManager.editEvent;

import android.net.Uri;

public class EventImageModel {
    String imageURL, imageId , imagePath;
    Uri uri;

    public EventImageModel(String imageURL, String imageId, String imagePath, Uri uri) {
        this.imageURL = imageURL;
        this.imageId = imageId;
        this.imagePath = imagePath;
        this.uri = uri;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
