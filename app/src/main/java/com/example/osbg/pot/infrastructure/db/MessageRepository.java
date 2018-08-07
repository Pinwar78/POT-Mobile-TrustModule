package com.example.osbg.pot.infrastructure.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.osbg.pot.infrastructure.db.dao.MessageDao;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

import java.util.List;

public class MessageRepository {
    private MessageDao mMessageDao;
    private LiveData<List<MessageEntity>> mMessageList;

    public MessageRepository(Application application){
        MessagingDatabase msgDb = MessagingDatabase.getDatabase(application);
        mMessageDao = msgDb.messageDao();
        mMessageList = mMessageDao.getAllMessages();
    }

    public LiveData<List<MessageEntity>> getAllMessages(){
        return mMessageList;
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
}
