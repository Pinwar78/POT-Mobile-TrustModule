package com.example.osbg.pot.ui.messaging;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.Message;
import com.example.osbg.pot.domain_models.ReceivedMessage;
import com.example.osbg.pot.domain_models.SentMessage;

import java.util.ArrayList;

/**
 * MessageListAdapter class to construct the Messages view.
 */

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private final LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Message> mMessages;

    public MessageListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mMessages != null){
            return mMessages.size();
        }
        return 0;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);

        if (mMessages.get(position) instanceof SentMessage) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReceivedMessage message = (ReceivedMessage) mMessages.get(position);
        ((ReceivedMessageHolder) holder).bind(message);
    }

    public void setMessages(ArrayList<Message> messages){
        this.mMessages = messages;
        notifyDataSetChanged();
    }
}
