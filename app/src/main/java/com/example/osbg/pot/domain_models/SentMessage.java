package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

public class SentMessage extends Message{

    public SentMessage(String seqno, String contactKey, String text, String time) {
        super(seqno, contactKey, text, time);
    }
    public SentMessage(MessageEntity messageEntity) {
        super(messageEntity);
    }
}
