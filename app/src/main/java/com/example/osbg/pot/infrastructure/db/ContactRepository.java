package com.example.osbg.pot.infrastructure.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.osbg.pot.infrastructure.db.dao.ContactDao;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;

import java.util.List;

public class ContactRepository {
    private ContactDao mContactDao;
    private LiveData<List<ContactEntity>> mContactList;

    public ContactRepository(Application application){
        MessagingDatabase msgDb = MessagingDatabase.getDatabase(application);
        mContactDao = msgDb.contactDao();
        mContactList = mContactDao.getAllContacts();
    }

    public LiveData<List<ContactEntity>> getAllContacts(){
        return mContactList;
    }

    public LiveData<ContactEntity> getByContactKey(String contactKey) {
        return mContactDao.getByContactKey(contactKey);
    }

    public void getByContactKeyAsync(String contactKey, final IDbCallback<ContactEntity> callback){
        final LiveData<ContactEntity> entityLiveData = this.getByContactKey(contactKey);
        entityLiveData.observeForever(new Observer<ContactEntity>() {
            @Override
            public void onChanged(@Nullable ContactEntity contactEntity) {
                entityLiveData.removeObserver(this);
                callback.onSuccess(contactEntity);
            }
        });
    }

    public LiveData<ContactEntity> getByPubId(String pubId) {
        return mContactDao.getByPubId(pubId);
    }

    public void getByPubIdAsync(String pubId, final IDbCallback<ContactEntity> callback){
        final LiveData<ContactEntity> entityLiveData = this.getByPubId(pubId);
        entityLiveData.observeForever(new Observer<ContactEntity>() {
            @Override
            public void onChanged(@Nullable ContactEntity contactEntity) {
                entityLiveData.removeObserver(this);
                callback.onSuccess(contactEntity);
            }
        });
    }

    public void addContact(ContactEntity contact){
        new insertContactAsyncTask(mContactDao).execute(contact);
    }

    private static class insertContactAsyncTask extends AsyncTask<ContactEntity, Void, Void> {
        private ContactDao mAsyncContactDao;

        insertContactAsyncTask(ContactDao contactDao){
            mAsyncContactDao = contactDao;
        }

        @Override
        protected Void doInBackground(final ContactEntity... params){
            mAsyncContactDao.insert(params[0]);
            return null;
        }
    }

    public void updateContact(ContactEntity contact){
        new updateContactAsyncTask(mContactDao).execute(contact);
    }

    private static class updateContactAsyncTask extends AsyncTask<ContactEntity, Void, Void> {
        private ContactDao mAsyncContactDao;

        updateContactAsyncTask(ContactDao contactDao){
            mAsyncContactDao = contactDao;
        }

        @Override
        protected Void doInBackground(final ContactEntity... params){
            mAsyncContactDao.update(params[0]);
            return null;
        }
    }

    public void removeContact(ContactEntity contact){
        new removeContactAsyncTask(mContactDao).execute(contact);
    }

    public static class removeContactAsyncTask extends AsyncTask<ContactEntity, Void, Void>{
        private ContactDao mAsyncContactDao;

        removeContactAsyncTask(ContactDao contactDao){
            mAsyncContactDao = contactDao;
        }

        @Override
        public Void doInBackground(final ContactEntity... params){
            mAsyncContactDao.delete(params[0]);
            return null;
        }
    }
}
