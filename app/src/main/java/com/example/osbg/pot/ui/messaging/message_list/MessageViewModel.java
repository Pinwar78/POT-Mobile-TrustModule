package com.example.osbg.pot.ui.messaging.message_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.domain_models.Message;
import com.example.osbg.pot.infrastructure.db.ContactRepository;
import com.example.osbg.pot.infrastructure.db.MessageRepository;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;
import com.example.osbg.pot.services.messaging.MessagingService;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository mMessageRepo;
    private ContactRepository mContactRepo;
    private MessagingService mMessagingService;
    private LiveData<List<MessageEntity>> mMessageList;
    private Contact contact;
    private Application application;

    public MessageViewModel(Application application){
        super(application);
        this.application = application;
        mContactRepo = new ContactRepository(application);
        mMessagingService = new MessagingService(application);
    }

    public void setContact(Contact contact){
        this.contact = contact;
    }

    public void loadMessages(){
        mMessageRepo = new MessageRepository(application);
        mMessageList = mMessageRepo.getAllByContactKey(contact.getContactkey());
    }

    public LiveData<List<MessageEntity>> getmMessageList() {
        return mMessageList;
    }

    public Contact getContact() {
        return contact;
    }

    public void sendMessage(String text){
        Message newMessage = new Message(null, contact.getContactkey(), text, Long.toString(System.currentTimeMillis() / 1000L));
        mMessagingService.addNewMessage(newMessage, "sent");
        mMessagingService.sendMessage(newMessage);
    }

    public void removeAllMessages(){
        mMessageRepo.removeAllMessagesByContactKey(contact.getContactkey());
    }

    public void removeContact(){
        mContactRepo.removeContact(new ContactEntity(contact));
    }
}
