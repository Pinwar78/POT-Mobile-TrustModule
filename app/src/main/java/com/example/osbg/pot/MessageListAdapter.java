package com.example.osbg.pot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * MessageListAdapter class to construct the Messages view.
 */

public class MessageListAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<ReceivedMessage> mMessageList;

    public MessageListAdapter(Context context, ArrayList<ReceivedMessage> receivedMessagesList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mMessageList = receivedMessagesList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
        ReceivedMessageHolder holder = new ReceivedMessageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReceivedMessage message = (ReceivedMessage) mMessageList.get(position);
        ((ReceivedMessageHolder) holder).bind(message);
    }
}
