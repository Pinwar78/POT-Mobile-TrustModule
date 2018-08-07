package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

public class SentMessage extends Message{

    public SentMessage(Contact sender, String text, String time) {
        super(sender, text, time);
    }
    public SentMessage(MessageEntity messageEntity) {
        super(messageEntity);
    }
}
