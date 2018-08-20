package com.example.osbg.pot.ui.messaging.contact_list;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.infrastructure.db.IDbCallback;
import com.example.osbg.pot.infrastructure.db.MessageRepository;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;

import java.util.ArrayList;

/**
 * ContactListAdapter class to construct the Contacts view.
 */

public class ContactListAdapter extends RecyclerView.Adapter {
    private final LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Contact> mContacts;
    private final OnContactItemClickListener clickListener;
    private MessageRepository messageRepo;

    public ContactListAdapter(Context context, OnContactItemClickListener clickListener) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.clickListener = clickListener;
        messageRepo = new MessageRepository((Application) mContext.getApplicationContext());
    }

    @Override
    public int getItemCount() {
        if (mContacts != null){
            return mContacts.size();
        }
        return 0;
    }

    // Determines the appropriate ViewType according to the sender of the contact.
    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Contact contact = mContacts.get(position);
        ((ContactItemHolder) holder).bindContact(contact, clickListener);

        messageRepo.getLastByContactKeyAsync(contact.getContactkey(), new IDbCallback<MessageEntity>() {
            @Override
            public void onSuccess(MessageEntity messageEntity) {
                if (messageEntity != null){
                    ((ContactItemHolder) holder).bindLastMessage(messageEntity.message_body);
                }
            }
        });
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.mContacts = contacts;
        notifyDataSetChanged();
    }
}
