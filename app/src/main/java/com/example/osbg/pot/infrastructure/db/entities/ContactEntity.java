package com.example.osbg.pot.infrastructure.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import com.example.osbg.pot.domain_models.Contact;

@Entity(primaryKeys = {"contactkey"},
        tableName = "contacts",
        indices = {@Index(value = {"contactkey"}, unique = true)})
//        foreignKeys = @ForeignKey(entity = Message.class,
//        parentColumns = "id",
//        childColumns = "contactkey"))
public class ContactEntity {
    @NonNull
    public String contactkey;
    public String name;
    public String pubid;
    public String senderkey;
    public String aeskey;

    public ContactEntity(String contactkey, String name, String pubid, String senderkey, String aeskey){
        this.contactkey = contactkey;
        this.name = name;
        this.pubid = pubid;
        this.senderkey = senderkey;
        this.aeskey = aeskey;
    }

    public ContactEntity(Contact contact){
        this.contactkey = contact.getContactkey();
        this.name = contact.getName();
        this.pubid = contact.getPubid();
        this.senderkey = contact.getSenderkey();
        this.aeskey = contact.getAeskey();
    }
}
