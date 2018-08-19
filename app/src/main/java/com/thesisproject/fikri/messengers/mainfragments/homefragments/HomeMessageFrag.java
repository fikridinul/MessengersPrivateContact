package com.thesisproject.fikri.messengers.mainfragments.homefragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;
import com.thesisproject.fikri.messengers.messages.ChatListAdapter;
import com.thesisproject.fikri.messengers.messages.ChatMessage;
import com.thesisproject.fikri.messengers.messages.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class HomeMessageFrag extends Fragment {

    Toolbar toolbar;
    ImageView ivMainChat;
    Contact contacts;
    FrameLayout frameLayout;
    ListView listViewMainChat;
    DatabaseHandler dbHandler;
    TextView name, content, time;
    ChatMessage currentChatMessage;
    ChatRoom chatRoom = new ChatRoom();
    FragmentTransaction fragmentTransaction;
    ArrayAdapter<ChatMessage> chatMainAdapter;
    List<ChatMessage> messages = new ArrayList<>();
    Uri path = Uri.parse("android.resource://com.thesisproject.fikri.messengers/drawable/" + R.drawable.ic_initial_acc);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_home_message, container, false);
        dbHandler = new DatabaseHandler(getActivity());
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        listViewMainChat = (ListView) frameLayout.findViewById(R.id.listViewMess);

        populateList();

        listViewMainChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentChatMessage = messages.get(position);
                if (dbHandler.chekcHideMode("hidden")) {
                    Contact conts = dbHandler.getContact(currentChatMessage.getMessageNumber());
                    chatRoom.passNameWith(currentChatMessage.getMessageNumber(), conts.getName());
                    movePage(currentChatMessage.getMessageWith(), chatRoom);
                } else {
                    chatRoom.passNameWith(currentChatMessage.getMessageNumber(), currentChatMessage.getMessageWith());
                    movePage(currentChatMessage.getMessageWith(), chatRoom);
                }
            }
        });

        listViewMainChat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentChatMessage = messages.get(position);
                if (dbHandler.chekcHideMode("hidden")) {
                    dbHandler.deleteMessage(currentChatMessage.getMessageNumber(), "whole");
                    messages.remove(position);
                    chatMainAdapter.notifyDataSetChanged();
                } else {

                }
                return false;
            }
        });
        return frameLayout;
    }

    public void populateList() {

        if (dbHandler.chekcHideMode("hidden")) {
            messages = dbHandler.checkAndGetLastChat();
        } else {
            messages = dbHandler.checkAndGetLastChatProvider();
        }
        chatMainAdapter = new ContactListAdapter();
        listViewMainChat.setAdapter(chatMainAdapter);
    }

    private class ContactListAdapter extends ArrayAdapter<ChatMessage> {
        public ContactListAdapter() {
            super (getActivity(), R.layout.list_view_main_message, messages);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.list_view_main_message, parent, false);

            currentChatMessage = messages.get(position);

//            name = (TextView) view.findViewById(R.id.textNameChat);
//            name.setText(currentChatMessage.getMessageWith());
            content = (TextView) view.findViewById(R.id.textContentChat);
            content.setText(currentChatMessage.getMessageText());
            time = (TextView) view.findViewById(R.id.textTimeChat);
            time.setText(ChatListAdapter.SIMPLE_DATE_FORMAT.format(currentChatMessage.getMessageTime()));

            if (dbHandler.chekcHideMode("hidden")) {
                contacts = dbHandler.getContact(currentChatMessage.getMessageNumber());
                name = (TextView) view.findViewById(R.id.textNameChat);
                name.setText(contacts.getName());
                ivMainChat = (ImageView) view.findViewById(R.id.ivChatMain);
                ivMainChat.setImageURI(contacts.getImageURI());
            } else {
                name = (TextView) view.findViewById(R.id.textNameChat);
                name.setText(currentChatMessage.getMessageWith());
                ivMainChat = (ImageView) view.findViewById(R.id.ivChatMain);
                ivMainChat.setImageURI(path);
            }

            return view;
        }

    }

    public void movePage(String namePage, Fragment nameFragment) {
        //Handle moving page and change toolbar title
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nameFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(namePage);
    }

}
