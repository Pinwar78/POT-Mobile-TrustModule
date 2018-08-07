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
    public String name;
    public String pubid;
    @NonNull
    public String contactkey;
    public String sendkey;
    public String aeskey;

    public ContactEntity(String name, String pubid, String contactkey, String sendkey, String aeskey){
        this.name = name;
        this.pubid = pubid;
        this.contactkey = contactkey;
        this.sendkey = sendkey;
        this.aeskey = aeskey;
    }

    public Contact toContactObject(){
        return new Contact(name, pubid, contactkey, sendkey);
    }
}
