package com.example.osbg.pot.infrastructure.db;

import android.arch.persistence.room.Entity;

public interface IDbCallback<T>{

    void onSuccess(T entity);
}
