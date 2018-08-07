package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

public abstract class Message {
    protected Contact contact;
    protected String text;
    protected String date;

    public Message(Contact sender, String text, String date) {
        this.contact = sender;
        this.text = text;
        this.date = date;
    }

    public Message(MessageEntity messageEntity){
        this.contact = new Contact("test_name", messageEntity.contactkey, messageEntity.contactkey, "aeskey");
        this.text = messageEntity.message_body;
        this.date = messageEntity.date;
    }

    public Contact getContact() {
        return contact;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }
}
