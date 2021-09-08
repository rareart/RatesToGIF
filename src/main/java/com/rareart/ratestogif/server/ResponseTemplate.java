package com.rareart.ratestogif.server;

public class ResponseTemplate {
    private final int status;
    private final String msg;
    private final String imageURL;

    public ResponseTemplate(int status, String msg, String imageURL) {
        this.status = status;
        this.msg = msg;
        this.imageURL = imageURL;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getImageURL() {
        return imageURL;
    }
}
