package com.example.hp.heartrytcare.db;

public class MessageData {
    private String to;
    private NotificationData notification;

    public MessageData(String to, NotificationData notification) {
        this.to = to;
        this.notification = notification;
    }

}
