package com.example.osbg.pot.domain_models;

public class Contact {
    private String name;
    private String pubid;
    private String contactkey;
    private String senderkey;

    public Contact(String name, String contactkey, String senderkey, String pubkey){
        this.name = name;
        this.contactkey = contactkey;
        this.senderkey = senderkey;
    }

    public void setName(String newName){
        newName.trim();
        if (newName.length() > 0){
            name = newName;
        }
    }

    public String getName() {
        return name;
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
}
