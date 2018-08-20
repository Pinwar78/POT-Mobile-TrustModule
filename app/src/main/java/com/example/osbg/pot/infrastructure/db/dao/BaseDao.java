package com.example.osbg.pot.infrastructure.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

@Dao
public abstract class BaseDao<T>{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(T... t);

    @Update
    public abstract void update(T t);

    @Delete
    public abstract void delete(T t);
}
