package com.example.osbg.pot.ui.messaging.contact_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.Contact;
import com.squareup.picasso.Picasso;

import agency.tango.android.avatarview.AvatarPlaceholder;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;

/*ContactItemHolder class that binds contact and creates layout for each contact*/

public class ContactItemHolder extends RecyclerView.ViewHolder {
    private TextView name, lastMessage;
    private AvatarView avatar;
    private PicassoLoader imageLoader;

    ContactItemHolder(final View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.contact_list_item_avatar);
        name = itemView.findViewById(R.id.contact_list_item_name);
        lastMessage = itemView.findViewById(R.id.contact_list_item_last_message);
        imageLoader = new PicassoLoader();
    }

    void bindContact(final Contact contact, final OnContactItemClickListener listener) {
        AvatarPlaceholder avatarPlaceholder = new AvatarPlaceholder(contact.getName());
        imageLoader.loadImage(avatar, avatarPlaceholder, null);
        name.setText(contact.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(contact);
            }
        });
    }

    void bindLastMessage(String lastMessage){
        this.lastMessage.setText(lastMessage);
    }
}