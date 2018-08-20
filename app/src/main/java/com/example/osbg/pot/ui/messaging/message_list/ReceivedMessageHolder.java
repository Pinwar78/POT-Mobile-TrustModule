package com.example.osbg.pot.ui.messaging.message_list;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.Contact;
import com.example.osbg.pot.domain_models.ReceivedMessage;

import agency.tango.android.avatarview.AvatarPlaceholder;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;

/*ReceiveMessageHolder class that binds messages and creates layout for each message*/

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageText, nameSender, timeText;
    private AvatarView avatar;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameSender = itemView.findViewById(R.id.text_message_sender);
        avatar = itemView.findViewById(R.id.contact_avatar);
    }

    void bind(ReceivedMessage message, Contact contact) {
        AvatarPlaceholder avatarPlaceholder = new AvatarPlaceholder(contact.getName());
        PicassoLoader imageLoader = new PicassoLoader();
        imageLoader.loadImage(avatar, avatarPlaceholder, null);
        nameSender.setText(contact.getName());
        messageText.setText(message.getText());
        timeText.setText(DateUtils.getRelativeTimeSpanString(
                Long.valueOf(message.getDate())*1000,
                System.currentTimeMillis(),
                0));
    }
}