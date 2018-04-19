package com.example.osbg.pot;

public class ReceivedMessage {
    private String messageSender;
    private String messageText;
    private String messageTime;

    public ReceivedMessage(String sender, String text, String time) {
        messageSender = sender;
        messageText = text;
        messageTime = time;
    }

    public String getSender() {
        return messageSender;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageTime() {
        return messageTime;
    }
}
