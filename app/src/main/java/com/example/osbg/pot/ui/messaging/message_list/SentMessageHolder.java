package com.example.osbg.pot.ui.messaging.message_list;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osbg.pot.R;
import com.example.osbg.pot.domain_models.SentMessage;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

/*ReceiveMessageHolder class that binds messages and creates layout for each message*/

public class SentMessageHolder extends RecyclerView.ViewHolder {
    private TextView messageText, nameSender, timeText;
    private ImageView profileImage;

    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
        timeText = itemView.findViewById(R.id.text_message_time);
    }

    void bind(SentMessage message) {
        messageText.setText(message.getText());
        timeText.setText(DateUtils.getRelativeTimeSpanString(
                Long.valueOf(message.getDate())*1000,
                System.currentTimeMillis(),
                0));
    }
}