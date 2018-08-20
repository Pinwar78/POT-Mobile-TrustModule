package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

public class ReceivedMessage extends Message{

    public ReceivedMessage(String seqno, String contactKey, String text, String time) {
        super(seqno, contactKey, text, time);
    }
    public ReceivedMessage(MessageEntity messageEntity) {
        super(messageEntity);
    }
}
