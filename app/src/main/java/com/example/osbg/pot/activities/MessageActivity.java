package com.example.osbg.pot.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.R;
import com.example.osbg.pot.ui.messaging.message_list.MessageListAdapter;

/*MessageActivity class that starts a new activity when the user clicks on a notification*/

public class MessageActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar receivedMessagesToolbar = (Toolbar) findViewById(R.id.received_messages_toolbar);
        setSupportActionBar(receivedMessagesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarReceivedMessagesTitle = findViewById(R.id.toolbarReceivedMessagesTitle);
        toolbarReceivedMessagesTitle.setText("Received Messages");

        mMessageRecycler = findViewById(R.id.recyclerview_message_list);
//        mMessageAdapter = new ContactListAdapter(this, LocationAsyncTask.notificationsList);
//        mMessageAdapter = new ContactListAdapter(this, MessagingPollingAsyncTask.notificationsList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

    @Override
    protected void onStart() {
//        mMessageAdapter.notifyDataSetChanged();
        super.onStart();
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
            case R.id.invite_node:
                Intent intent = new Intent(this, InviteNodeActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}