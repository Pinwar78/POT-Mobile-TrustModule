package com.example.osbg.pot.ui.messaging.invite_contact;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;

import com.example.osbg.pot.services.ContactInviter;
import com.example.osbg.pot.infrastructure.QrCodeGenerator;
import com.example.osbg.pot.services.INodeRequestCallback;

import org.json.JSONObject;

public class InviteContactViewModel extends AndroidViewModel{
    private ContactInviter contactInviter;
    private Context context;

    public InviteContactViewModel(Application application){
        super(application);
        contactInviter = new ContactInviter(application);
        context = application;
    }

    public void genInviteQrCode(INodeRequestCallback callback){
        new QrCodeGenerator(context).generate(getInviteString(), callback);
    }

    private String getInviteString(){
        return contactInviter.getInviteString();
    }
}
