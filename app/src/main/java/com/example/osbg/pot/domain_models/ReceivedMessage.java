package com.example.osbg.pot.domain_models;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

public class ReceivedMessage extends Message{

    public ReceivedMessage(Contact sender, String text, String time) {
        super(sender, text, time);
    }
    public ReceivedMessage(MessageEntity messageEntity) {
        super(messageEntity);
    }
}
