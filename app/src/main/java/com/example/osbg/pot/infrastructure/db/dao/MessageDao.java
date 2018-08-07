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
}
