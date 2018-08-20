package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

public class Message {
    protected String seqno;
    protected String contactKey;
    protected String text;
    protected String date;

    public Message(String seqno, String contactKey, String text, String date) {
        this.seqno = seqno;
        this.contactKey = contactKey;
        this.text = text;
        this.date = date;
    }

    public Message(MessageEntity messageEntity){
        this.seqno = messageEntity.seqno;
        this.contactKey = messageEntity.contactkey;
        this.text = messageEntity.message_body;
        this.date = messageEntity.date;
    }

    public String getContactKey() {
        return contactKey;
    }

    public String getSeqno() {
        return seqno;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public void setContactKey(String contactKey) {
        this.contactKey = contactKey;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
