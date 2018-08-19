package com.thesisproject.fikri.messengers.messages;


import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoom extends Fragment {

    String withNumb, withNumb1, withName;
    EditText chatEditText;
    ListView chatListView;
    ImageView enterChatView;
    ChatMessage currChatMessage;
    ChatListAdapter listAdapter;
    RelativeLayout relativeLayout;
    List<ChatMessage> chatMessages;
    DatabaseHandler dbHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        relativeLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_chat_room, container, false);
        chatEditText = (EditText) relativeLayout.findViewById(R.id.chat_edit_text1);
        enterChatView = (ImageView) relativeLayout.findViewById(R.id.enter_chat1);
        chatListView = (ListView) relativeLayout.findViewById(R.id.chat_list_view);
        dbHandler = new DatabaseHandler(getActivity());

        chatMessages = new ArrayList<>();

        //dbHandler.fetchAllMessage();

        enterChatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v==enterChatView) {
                    SmsManager smsMan = SmsManager.getDefault();
                    String bod = chatEditText.getText().toString();
                    if (dbHandler.chekcHideMode("hidden")) {
                        sendMessage(bod, "SELF", withName, withNumb);
                        try {
                            smsMan.sendTextMessage(withNumb1, null, bod, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Catch block", Log.getStackTraceString(e));
                        }
                    } else {
                        try {
                            smsMan.sendTextMessage(withNumb1, null, bod, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Catch block", Log.getStackTraceString(e));
                        }
                        ContentValues values = new ContentValues();
                        values.put(Telephony.Sms.ADDRESS, withNumb);
                        values.put(Telephony.Sms.BODY, chatEditText.getText().toString());
                        getActivity().getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
                        SystemClock.sleep(2000);
                        populateChatList();
                        listAdapter.notifyDataSetChanged();
                    }
                }
                chatEditText.setText("");
            }
        });

        chatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chatEditText.getText().toString().equals("")) {

                } else {
                    enterChatView.setImageResource(R.drawable.ic_chat_send_active);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    enterChatView.setImageResource(R.drawable.ic_chat_send);
                } else {
                    enterChatView.setImageResource(R.drawable.ic_chat_send_active);
                }
            }
        });

        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                currChatMessage = chatMessages.get(position);
                if (dbHandler.chekcHideMode("hidden")) {
                    dbHandler.deleteMessage(String.valueOf(currChatMessage.getMessageID()), "id");
                    chatMessages.remove(position);
                    listAdapter.notifyDataSetChanged();
                } else {
                    try {
                        int idsn = currChatMessage.getMessageID();
                        getActivity().getContentResolver().delete(Uri.parse("content://sms/" + idsn), null, null);
                        chatMessages.remove(position);
                        listAdapter.notifyDataSetChanged();
                    }catch(Exception e){
                        Log.e(this.toString(), "Error deleting sms", e);
                    }
                }
                    return false;
        }
    });

        populateChatList();

        return relativeLayout;
    }

    private void populateChatList() {
        if (dbHandler.chekcHideMode("hidden")) {
            chatMessages = dbHandler.getAllChat(withNumb);
        } else {
            chatMessages = dbHandler.fetchMessage(withNumb);
        }
        listAdapter = new ChatListAdapter(chatMessages, getContext());
        chatListView.setAdapter(listAdapter);
    }

/*    private ListView.OnItemLongClickListener longClickListViewChat = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            currChatMessage = chatMessages.get(position);
            dbHandler.deleteMessage(String.valueOf(currChatMessage.getMessageID()), "id");

            *//*try {
                int idsn = currChatMessage.getMessageID();
                getActivity().getContentResolver().delete(Uri.parse("content://sms/" + idsn), null, null);
                chatMessages.remove(position);
                listAdapter.notifyDataSetChanged();
            }catch(Exception e){
                Log.e(this.toString(), "Error deleting sms", e);
            }*//*
            return false;
        }
    };*/

    private void sendMessage(final String messageText, String userType, String with, String number) {
        if (messageText.trim().length()==0)
            return;

        final ChatMessage message =  new ChatMessage(1, with, userType, messageText, "2", new Date().getTime(), number);
        chatMessages.add(message);
        dbHandler.insertMessageChat(message);

        if (listAdapter!=null){
            listAdapter.notifyDataSetChanged();
        }

    }

    public void passNameWith(String numb, String with) {
        withNumb1 = "+62" + numb;
        withNumb = numb;
        withName = with;
    }

}
