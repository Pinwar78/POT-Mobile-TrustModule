package com.example.osbg.pot.infrastructure.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "messages"
//        foreignKeys = @ForeignKey(entity = ContactEntity.class,
//                parentColumns = "contactkey",
//                childColumns = "contactkey")
)
public class MessageEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String message_body;
    public String contactkey;
    public String date;
    public String type;

    public MessageEntity(String id, String message_body, String contactkey, String date, String type) {
        this.id = id;
        this.message_body = message_body;
        this.contactkey = contactkey;
        this.date = date;
        this.type = type;
    }
}
