package com.example.osbg.pot.infrastructure.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.osbg.pot.infrastructure.db.dao.MessageDao;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

import java.util.List;

public class MessageRepository {
    private MessageDao mMessageDao;

    public MessageRepository(Application application){
        MessagingDatabase msgDb = MessagingDatabase.getDatabase(application);
        mMessageDao = msgDb.messageDao();

    }

    public LiveData<List<MessageEntity>> getAllMessages(){
        return mMessageDao.getAllMessages();
    }

    public LiveData<List<MessageEntity>> getAllByContactKey(String contactKey){
        return mMessageDao.getByContactKey(contactKey);
    }
    public LiveData<MessageEntity> getLastByContactKey(String contactKey) {
        return mMessageDao.getLastByContactKey(contactKey);
    }
    public void getLastByContactKeyAsync(String contactKey, final IDbCallback<MessageEntity> callback){
        final LiveData<MessageEntity> messageEntityLiveData = this.getLastByContactKey(contactKey);
        messageEntityLiveData.observeForever(new Observer<MessageEntity>() {
            @Override
            public void onChanged(@Nullable MessageEntity messageEntity) {
                messageEntityLiveData.removeObserver(this);
                callback.onSuccess(messageEntity);
            }
        });
    }

    public void addMessage(MessageEntity message){
        new insertMessageAsyncTask(mMessageDao).execute(message);
    }

    private static class insertMessageAsyncTask extends AsyncTask<MessageEntity, Void, Void> {
        private MessageDao mAsyncMessageDao;

        insertMessageAsyncTask(MessageDao messageDao){
            mAsyncMessageDao = messageDao;
        }

        @Override
        protected Void doInBackground(final MessageEntity... params){
            mAsyncMessageDao.insert(params[0]);
            return null;
        }
    }

    public void removeMessage(MessageEntity message){
        new removeMessageAsyncTask(mMessageDao).execute(message);
    }

    public static class removeMessageAsyncTask extends AsyncTask<MessageEntity, Void, Void>{
        private MessageDao mAsyncMessageDao;

        removeMessageAsyncTask(MessageDao messageDao){
            mAsyncMessageDao = messageDao;
        }

        @Override
        public Void doInBackground(final MessageEntity... params){
            mAsyncMessageDao.delete(params[0]);
            return null;
        }
    }

    public void removeAllMessagesByContactKey(String contactKey){
        new removeAllMessagesByContactKeyAsyncTask(mMessageDao).execute(contactKey);
    }

    public static class removeAllMessagesByContactKeyAsyncTask extends AsyncTask<String, Void, Void>{
        private MessageDao mAsyncMessageDao;

        removeAllMessagesByContactKeyAsyncTask(MessageDao messageDao){
            mAsyncMessageDao = messageDao;
        }

        @Override
        public Void doInBackground(final String... params){
            mAsyncMessageDao.removeAllByContactKey(params[0]);
            return null;
        }
    }
}
