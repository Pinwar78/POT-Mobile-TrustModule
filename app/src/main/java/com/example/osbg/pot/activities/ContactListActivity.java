package com.example.osbg.pot.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osbg.pot.MainActivity;
import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.infrastructure.db.entities.ContactEntity;
import com.example.osbg.pot.services.messaging.MessagingService;
import com.example.osbg.pot.ui.messaging.contact_list.ContactListAdapter;
import com.example.osbg.pot.ui.messaging.contact_list.ContactListViewModel;
import com.example.osbg.pot.ui.messaging.contact_list.OnContactItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private RecyclerView mContactRecycler;
    private ContactListAdapter mContactAdapter;
    private ContactListViewModel mContactItemViewModel;
    private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getSharedPreferences(MessagingService.MESSAGING_PREFERENCES,0).getString("name", "").isEmpty()){
            showEditNameDialog();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar receivedContactsToolbar = findViewById(R.id.contact_list_toolbar);
        setSupportActionBar(receivedContactsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarReceivedContactsTitle = findViewById(R.id.toolbarContactListTitle);
        toolbarReceivedContactsTitle.setText("Contacts");

        mContactRecycler = findViewById(R.id.recyclerview_contact_list);
        mContactAdapter = new ContactListAdapter(this, new OnContactItemClickListener() {
            @Override
            public void onItemClick(Contact item) {
                ContactListActivity.this.onContactItemClick(item);
            }
        });
        mContactRecycler.setAdapter(mContactAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mContactRecycler.setLayoutManager(mLayoutManager);

        mContactItemViewModel = ViewModelProviders.of(this).get(ContactListViewModel.class);
        mContactItemViewModel.getmContactList().observe(this, new Observer<List<ContactEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ContactEntity> contacts){
                ArrayList<Contact> allContacts = new ArrayList<>();

                for (int i = 0; i < contacts.size(); i++){
                    allContacts.add(new Contact(contacts.get(i)));
                }
                mContactAdapter.setContacts(allContacts);
            }
        });
    }

    //creates the three dot settings message_list_menu inside the app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handles onclick events in the three dot settings message_list_menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_node:
                Intent inviteNodeIntent = new Intent(this, InviteNodeActivity.class);
                startActivity(inviteNodeIntent);
                break;
            case R.id.invite_contact:
                Intent inviteContactIntent = new Intent(this, InviteContactActivity.class);
                startActivity(inviteContactIntent);
                break;
            case R.id.edit_name:
                showEditNameDialog();
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

    private void onContactItemClick(Contact contact){
        Intent messagesIntent = new Intent(this, MessageListActivity.class);
        messagesIntent.putExtra("contact", contact);
        startActivity(messagesIntent);
    }

    private void showEditNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(getSharedPreferences(MessagingService.MESSAGING_PREFERENCES, 0).getString("name", ""));
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (newName.length() < 1 || newName.length() > 30){
                    Toast.makeText(ContactListActivity.this, "Your name should be between 1 and 20 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                mContactItemViewModel.setNewName(newName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
