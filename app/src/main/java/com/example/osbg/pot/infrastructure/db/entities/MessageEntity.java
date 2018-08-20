package com.example.osbg.pot.infrastructure.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.osbg.pot.domain_models.Message;

@Entity(tableName = "messages",
        indices = {@Index(value = {"seqno", "contactkey"}, unique = true)}
//        foreignKeys = @ForeignKey(entity = ContactEntity.class,
//                parentColumns = "contactkey",
//                childColumns = "contactkey")
)
public class MessageEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String seqno;
    public String message_body;
    public String contactkey;
    public String date;
    public String type;

    public MessageEntity(String seqno, String message_body, String contactkey, String date, String type) {
        this.seqno = seqno;
        this.message_body = message_body;
        this.contactkey = contactkey;
        this.date = date;
        this.type = type;
    }

    public MessageEntity(String seqno, Message message, String type){
        this.seqno = seqno;
        this.message_body = message.getText();
        this.contactkey = message.getContactKey();
        this.date = message.getDate();
        this.type = type;
    }
}
