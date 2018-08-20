package com.example.osbg.pot.ui.messaging.contact_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.infrastructure.db.ContactRepository;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.services.MessagingService;

import java.util.List;

public class ContactListViewModel extends AndroidViewModel {
    private ContactRepository mContactRepo;
    private LiveData<List<ContactEntity>> mContactList;
    private MessagingService messagingService;

    public ContactListViewModel(Application application){
        super(application);
        mContactRepo = new ContactRepository(application);
        mContactList = mContactRepo.getAllContacts();
        messagingService = new MessagingService(application);
    }

    public LiveData<List<ContactEntity>> getmContactList() {
        return mContactList;
    }

    public void addContact(ContactEntity contact){
        mContactRepo.addContact(contact);
    }

    public void setNewName(final String newName){
        messagingService.setProfileName(newName);
        mContactList.observeForever(new Observer<List<ContactEntity>>() {
                                        @Override
                                        public void onChanged(@Nullable List<ContactEntity> contactEntities) {
                                            for (int i = 0; i < contactEntities.size(); i++){
                                                ContactEntity contact = contactEntities.get(i);
                                                messagingService.sendContactInfo(new Contact(contact));
                                            }
                                            mContactList.removeObserver(this);
                                        }
                                    });
    }
}
