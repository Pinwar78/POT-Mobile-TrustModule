package com.example.osbg.pot.ui.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.ReceivedMessage;

/*ReceiveMessageHolder class that binds messages and creates layout for each message*/

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, nameSender, timeText;
    ImageView profileImage;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameSender = (TextView) itemView.findViewById(R.id.text_message_sender);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
    }

    void bind(ReceivedMessage message) {
        nameSender.setText(message.getContact().getName());
        messageText.setText(message.getText());
        timeText.setText(message.getDate());
    }
}