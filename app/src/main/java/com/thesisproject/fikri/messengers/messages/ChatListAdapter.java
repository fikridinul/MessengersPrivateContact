package com.thesisproject.fikri.messengers.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesisproject.fikri.messengers.R;

import java.text.SimpleDateFormat;
import java.util.List;


public class ChatListAdapter extends BaseAdapter {

    private List<ChatMessage> chatMessages;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatListAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ChatMessage message = chatMessages.get(position);
        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.getUserType().equals("1")) {//"OTHER")) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_other, null, false);
                holder1 = new ViewHolder1();

                holder1.messageTextVIew = (TextView) v.findViewById(R.id.message_text);
                holder1.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();
            }

            holder1.messageTextVIew.setText(message.getMessageText());
            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));
        } else {//if (message.getUserType().equals("2") ) {//"SELF")) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_self, null, false);

                holder2 = new ViewHolder2();

                holder2.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder2.timeTextView = (TextView) v.findViewById(R.id.time_text);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                v.setTag(holder2);
            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();
            }

            holder2.messageTextView.setText(message.getMessageText());
            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

            if (message.getMessageStatus().equals("2")) {//"DELIVERED")) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_double_tick));
            } else {//if (message.getMessageStatus().equals("SENT")) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_single_tick));
            }

        }

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        int ord;
        if (message.getUserType().equals("1")) {
            ord = 0;
        } else {
            ord = 1;
        }
        return ord;
    }

    private class ViewHolder1 {
        public TextView messageTextVIew;
        public TextView timeTextView;
    }

    private class ViewHolder2 {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;
    }

}
