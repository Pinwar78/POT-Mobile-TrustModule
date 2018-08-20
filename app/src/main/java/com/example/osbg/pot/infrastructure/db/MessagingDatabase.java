package com.example.osbg.pot.infrastructure.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.osbg.pot.infrastructure.db.dao.ContactDao;
import com.example.osbg.pot.infrastructure.db.dao.MessageDao;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

@Database(entities = {ContactEntity.class, MessageEntity.class}, version = 2)
public abstract class MessagingDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
    public abstract MessageDao messageDao();

    private static MessagingDatabase INSTANCE;

    public static MessagingDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MessagingDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MessagingDatabase.class, "messaging_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
