package com.example.osbg.pot.infrastructure.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

import java.util.List;

@Dao
public abstract class MessageDao extends BaseDao<MessageEntity> {
    @Query("SELECT * FROM messages")
    public abstract LiveData<List<MessageEntity>> getAllMessages();

    @Query("SELECT * FROM messages WHERE id IN (:ids)")
    public abstract List<MessageEntity> getByIds(int[] ids);

    @Query("SELECT * FROM messages WHERE contactkey LIKE :contactKey")
    public abstract LiveData<List<MessageEntity>> getByContactKey(String contactKey);

    @Query("SELECT * FROM messages WHERE contactkey LIKE :contactKey ORDER BY ID DESC LIMIT 1")
    public abstract LiveData<MessageEntity> getLastByContactKey(String contactKey);

    @Query("DELETE FROM messages WHERE contactkey LIKE :contactKey")
    public abstract void removeAllByContactKey(String contactKey);
}