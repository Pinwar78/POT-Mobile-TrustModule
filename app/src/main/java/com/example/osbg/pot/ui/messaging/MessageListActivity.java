package com.example.osbg.pot.ui.messaging;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.ReceivedMessage;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;
import com.example.osbg.pot.domain_models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private MessageViewModel mMessageViewModel;
    private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = (RecyclerView) findViewById(R.id.recyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(mLayoutManager);

        mMessageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        mMessageViewModel.getmMessageList().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(@Nullable final List<MessageEntity> messages){
                ArrayList<Message> allMessages = new ArrayList<Message>();

                for (int i = 0; i < messages.size(); i++){
                    allMessages.add(new ReceivedMessage(messages.get(i)));
                }
                mMessageAdapter.setMessages(allMessages);
                mMessageRecycler.scrollToPosition(messages.size() - 1);
            }
        });
    }
}