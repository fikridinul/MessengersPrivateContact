package com.thesisproject.fikri.messengers.mainfragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class DeleteFragment extends Fragment {

    Contact currCont;
    FrameLayout frameLayout;
    ListView contactListView;
    DatabaseHandler dbHandler;
    AlertDialog.Builder builder1;
    ArrayAdapter<Contact> contactAdapter;
    List<Contact> Contacts = new ArrayList<Contact>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbHandler = new DatabaseHandler(getActivity());
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_delete, container, false);
        contactListView = (ListView) frameLayout.findViewById(R.id.listViewDel);
        builder1 = new AlertDialog.Builder(getContext());

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                builder1.setMessage("Are you sure to delete this contact?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                currCont = Contacts.get(position);
                                dbHandler.deleteContact(currCont);
                                dbHandler.deleteMessage(currCont.getPhone(), "whole");
                                dbHandler.deleteCall(currCont.getName(), "all");
                                Contacts.remove(position);
                                contactAdapter.notifyDataSetChanged();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        if (dbHandler.getContactsCount() != 0) {
            Contacts.clear();
            Contacts.addAll(dbHandler.getAllContacts());
        }

        populateList();
        return frameLayout;
    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }

    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super (getActivity(), R.layout.list_view_contacts, Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.list_view_contacts, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.textViewName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.textViewPhone);
            phone.setText("+62" + currentContact.getPhone());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactIma);
            ivContactImage.setImageURI(currentContact.getImageURI());

            return view;
        }
    }

}
