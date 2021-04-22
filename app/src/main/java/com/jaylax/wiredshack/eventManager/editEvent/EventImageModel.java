package com.jaylax.wiredshack.eventManager.editEvent;

import android.net.Uri;

public class EventImageModel {
    String imageURL, imageId ;
    Uri uri;

    public EventImageModel(String imageURL, String imageId, Uri uri) {
        this.imageURL = imageURL;
        this.imageId = imageId;
        this.uri = uri;
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
