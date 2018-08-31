package com.example.osbg.pot.services.api;

public interface INodeRequestCallback<T> {
    void onSuccess(T response);
}
