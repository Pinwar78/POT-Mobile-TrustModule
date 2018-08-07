package com.example.osbg.pot.ui.messaging;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.osbg.pot.infrastructure.db.MessageRepository;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository mMessageRepo;
    private LiveData<List<MessageEntity>> mMessageList;

    public MessageViewModel(Application application){
        super(application);
        mMessageRepo = new MessageRepository(application);
        mMessageList = mMessageRepo.getAllMessages();
    }

    public LiveData<List<MessageEntity>> getmMessageList() {
        return mMessageList;
    }

    public void addMessage(MessageEntity message){
        mMessageRepo.addMessage(message);
    }
}
