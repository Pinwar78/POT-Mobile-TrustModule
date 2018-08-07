package com.example.osbg.pot.ui.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.SentMessage;

/*ReceiveMessageHolder class that binds messages and creates layout for each message*/

public class SentMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageText, nameSender, timeText;
    private ImageView profileImage;

    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
        nameSender = itemView.findViewById(R.id.text_message_sender);
        profileImage = itemView.findViewById(R.id.image_message_profile);
    }

    void bind(SentMessage message) {
        nameSender.setText(message.getContact().getName());
        messageText.setText(message.getText());
        timeText.setText(message.getDate());
    }
}