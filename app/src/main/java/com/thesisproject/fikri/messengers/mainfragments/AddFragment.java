package com.thesisproject.fikri.messengers.mainfragments;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.contacts.ContactDetails;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;
import com.thesisproject.fikri.messengers.mainfragments.homefragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    Uri wholeContUri;
    String msg, msg1;
    Toolbar toolbar;
    FrameLayout frameLayout;
    FloatingActionButton fab;
    ListView contactListView;
    DatabaseHandler dbHandler;
    Fragment homeFragment;
    AlertDialog.Builder builder1;
    List<String> nameArray, numbArray, namenumb;
    ArrayAdapter<String> contactAdapterString;
    Uri path = Uri.parse("android.resource://com.thesisproject.fikri.messengers/drawable/" + R.drawable.ic_initial_acc);
    //List<Contact> Contacts = new ArrayList<Contact>();
    FragmentTransaction fragmentTransaction;
    ContactDetails contactDetails = new ContactDetails();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_add, container, false);
        contactListView = (ListView) frameLayout.findViewById(R.id.listViewAdd);
        dbHandler = new DatabaseHandler(getContext());
        contactAdapterString = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        builder1 = new AlertDialog.Builder(getContext());
        homeFragment = new HomeFragment();
        nameArray  = new ArrayList<String>();
        numbArray = new ArrayList<String>();
        namenumb = new ArrayList<String>();

        nameArray.clear();
        numbArray.clear();

        wholeContUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phones = getActivity().getContentResolver().query(wholeContUri, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );

        if (phones.moveToFirst()) {
            do {

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String pnumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (!pnumb.contains("-")) {
                    namenumb.add(name + ", " + pnumb);
                    nameArray.add(name);
                    numbArray.add(pnumb);
                }

            } while (phones.moveToNext());
        }
        phones.close();

        contactAdapterString.addAll(namenumb);
        contactListView.setAdapter(contactAdapterString);

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {

                builder1.setMessage("Are you sure to add this contact?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                msg = nameArray.get(position);
                                msg1 = numbArray.get(position);

                                if (msg1.substring(0,3).equals("+62")) {
                                    msg1 = msg1.substring(3, msg1.length());
                                } else if (msg1.substring(0,1).equals("0")) {
                                    msg1 = msg1.substring(1, msg1.length());
                                }
                                Contact contact = new Contact(dbHandler.getContactsCount(), msg, msg1, path);
                                if (!contactExists(contact)) {
                                    dbHandler.createContact(contact);
                                    //Contacts.add(contact);
                                    Toast.makeText(getContext(), msg + " " + msg1, Toast.LENGTH_SHORT).show();
                                    deleteContact(getActivity(), msg1, msg);
                                    movePage("Home", homeFragment);
                                    return;
                                }
                                Toast.makeText(getContext(), msg + " already exist!", Toast.LENGTH_SHORT).show();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movePage("Add Contact", contactDetails);
            }
        });

        fab.show();
        return frameLayout;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fab.isShown()) {
            fab.hide();
        }
    }

    private boolean contactExists(Contact contact) {
        String name = contact.getName();
        int contactCount = dbHandler.getAllContacts().size();

        for (int i = 0; i < contactCount; i++) {
            if (name.compareToIgnoreCase(dbHandler.getAllContacts().get(i).getName()) == 0) {
                return true;
            }
        }
        return false;
    }

    public void movePage(String namePage, Fragment nameFragment) {
        //Handle moving page and change toolbar title
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nameFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(namePage);
    }

    public boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }

}
