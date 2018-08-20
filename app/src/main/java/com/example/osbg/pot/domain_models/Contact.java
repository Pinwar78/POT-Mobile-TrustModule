package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;

import java.io.Serializable;

public class Contact implements Serializable{
    private String name;
    private String pubid;
    private String contactkey;
    private String senderkey;
    private String aeskey;

    public Contact(String name, String pubid, String contactkey, String senderkey, String aeskey){
        this.name = name;
        this.pubid = pubid;
        this.contactkey = contactkey;
        this.senderkey = senderkey;
        this.aeskey = aeskey;
    }

    public Contact(ContactEntity contactEntity){
        this.name = contactEntity.name;
        this.pubid = contactEntity.pubid;
        this.contactkey = contactEntity.contactkey;
        this.senderkey = contactEntity.senderkey;
        this.aeskey = contactEntity.aeskey;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName){
        newName = newName.trim();
        if (newName.length() > 0){
            name = newName;
        }
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getContactkey() {
        return contactkey;
    }

    public void setContactkey(String contactkey) {
        this.contactkey = contactkey;
    }

    public String getSenderkey() {
        return senderkey;
    }

    public void setSenderkey(String senderkey) {
        this.senderkey = senderkey;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }
}
