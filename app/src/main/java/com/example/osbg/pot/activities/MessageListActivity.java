package com.example.osbg.pot.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.domain_models.ReceivedMessage;
import com.example.osbg.pot.domain_models.SentMessage;
import com.example.osbg.pot.infrastructure.db.entities.MessageEntity;
import com.example.osbg.pot.domain_models.Message;
import com.example.osbg.pot.ui.messaging.message_list.MessageListAdapter;
import com.example.osbg.pot.ui.messaging.message_list.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private MessageViewModel mMessageViewModel;
    private LinearLayoutManager mLayoutManager;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (contact == null){
            contact = (Contact) getIntent().getSerializableExtra("contact");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        Toolbar receivedMessagesToolbar = findViewById(R.id.received_messages_toolbar);
        setSupportActionBar(receivedMessagesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final TextView toolbarReceivedMessagesTitle = findViewById(R.id.toolbarReceivedMessagesTitle);
        toolbarReceivedMessagesTitle.setText(contact.getName());

        mMessageRecycler = findViewById(R.id.recyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, contact);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(mLayoutManager);

        mMessageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        mMessageViewModel.setContact(contact);
        mMessageViewModel.loadMessages();
        mMessageViewModel.getmMessageList().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(@Nullable final List<MessageEntity> messages){
                ArrayList<Message> allMessages = new ArrayList<>();

                for (int i = 0; i < messages.size(); i++){
                    if (messages.get(i).type.equals("received")){
                        allMessages.add(new ReceivedMessage(messages.get(i)));
                    } else {
                        allMessages.add(new SentMessage(messages.get(i)));
                    }
                }
                mMessageAdapter.setMessages(allMessages);
                mMessageRecycler.scrollToPosition(messages.size() - 1);
            }
        });

        Button sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputField = findViewById(R.id.edittext_chatbox);
                String text = inputField.getText().toString().trim();
                if (text.length() > 0){
                    mMessageViewModel.sendMessage(text);
                }
                inputField.setText("");
            }
        });
    }

    //creates the three dot settings message_list_menu inside the app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handles onclick events in the three dot settings message_list_menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_all_messages:
                mMessageViewModel.removeAllMessages();
                mMessageAdapter.notifyDataSetChanged();
                break;
            case R.id.remove_contact:
                mMessageViewModel.removeContact();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}