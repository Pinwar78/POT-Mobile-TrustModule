package com.example.osbg.pot.infrastructure.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;

import java.util.List;

@Dao
public abstract class ContactDao extends BaseDao<ContactEntity>{
    @Query("SELECT * FROM contacts")
    abstract LiveData<List<ContactEntity>> getAllContacts();

    @Query("SELECT * FROM contacts WHERE contactkey IN (:contactKeys)")
    abstract List<ContactEntity> getByContactKeys(String[] contactKeys);

    @Query("SELECT * FROM contacts WHERE contactkey LIKE :contactKey")
    abstract List<ContactEntity> getByContactKey(String contactKey);

    @Query("SELECT * FROM contacts WHERE name LIKE :name LIMIT 1")
    abstract ContactEntity findByName(String name);
}
