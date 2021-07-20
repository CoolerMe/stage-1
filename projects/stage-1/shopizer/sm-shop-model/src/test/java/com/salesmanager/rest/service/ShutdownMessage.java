package com.salesmanager.rest.service;

public class ShutdownMessage {

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ShutdownMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
