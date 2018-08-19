package com.thesisproject.fikri.messengers.mainfragments.homefragments;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.contacts.ContactDetails;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;
import com.thesisproject.fikri.messengers.messages.ChatMessage;
import com.thesisproject.fikri.messengers.messages.ChatRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeContactFrag extends Fragment {

    Toolbar toolbar;
    Uri wholeContUri;
    Fragment homeFragment;
    TextView name, phone;
    Contact currentContact;
    FrameLayout frameLayout;
    ImageView ivContactImage;
    ListView contactListView;
    DatabaseHandler dbHandler;
    ImageButton imageButtonCall;
    ContactDetails contactDetails;
    ChatRoom chatRoom = new ChatRoom();
    ArrayAdapter<Contact> contactAdapter;
    ArrayAdapter<String> contactAdapterString;
    FragmentTransaction fragmentTransaction;
    List<Contact> Contacts = new ArrayList<>();
    Uri path = Uri.parse("android.resource://com.thesisproject.fikri.messengers/drawable/" + R.drawable.ic_initial_acc);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_home_contact, container, false);
        contactDetails = new ContactDetails();
        dbHandler = new DatabaseHandler(getActivity());
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        contactListView = (ListView) frameLayout.findViewById(R.id.listViewContact);
        contactAdapterString = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        homeFragment = new HomeFragment();

        if (dbHandler.chekcHideMode("hidden")) {
            if (dbHandler.getContactsCount() != 0) {
                Contacts.clear();
                Contacts.addAll(dbHandler.getAllContacts());
            }
        } else {
            Contacts.clear();
            fetchContPhone();
        }

        populateList();

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (dbHandler.chekcHideMode("hidden")) {
                    currentContact = Contacts.get(position);
                    contactDetails.getContactDeatails(currentContact.getId(), currentContact.getName(), currentContact.getPhone(), currentContact.getImageURI());
                    movePage("Edit Contact", contactDetails);
                }
                return false;
            }
        });

        return frameLayout;
    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }

    private class ContactListAdapter extends ArrayAdapter<Contact> {

        public ContactListAdapter() {
            super(getActivity(), R.layout.list_view_contacts, Contacts);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.list_view_contacts, parent, false);

            currentContact = Contacts.get(position);

            if (dbHandler.chekcHideMode("hidden")) {
                name = (TextView) view.findViewById(R.id.textViewName);
                name.setText(currentContact.getName());
                phone = (TextView) view.findViewById(R.id.textViewPhone);
                phone.setText("+62" + currentContact.getPhone());
                ivContactImage = (ImageView) view.findViewById(R.id.ivContactIma);
                ivContactImage.setImageURI(currentContact.getImageURI());
                imageButtonCall = (ImageButton) view.findViewById(R.id.ibCallButt);
                imageButtonCall.setVisibility(view.VISIBLE);
            } else {
                name = (TextView) view.findViewById(R.id.textViewName);
                name.setText(currentContact.getName());
                phone = (TextView) view.findViewById(R.id.textViewPhone);
                phone.setText(currentContact.getPhone());
                ivContactImage = (ImageView) view.findViewById(R.id.ivContactIma);
                ivContactImage.setImageURI(path);
                imageButtonCall = (ImageButton) view.findViewById(R.id.ibCallButt);
                imageButtonCall.setVisibility(view.VISIBLE);
                imageButtonCall.setImageResource(R.drawable.ic_create_24dp);
            }

            imageButtonCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO-deletlog    : find away to delete contact log after performing outgoing call
                    //TODO-extraintent : give the intent an extra information so it can be detected by receiver whether the call came from private contact or default

                    currentContact = Contacts.get(position);

                    if (dbHandler.chekcHideMode("hidden")) {
                        Intent iniCall = new Intent(Intent.ACTION_CALL);
                        iniCall.setData(Uri.parse("tel:+62" + currentContact.getPhone()));
                        startActivity(iniCall);
                    } else {
                        String address = currentContact.getPhone();
                        if (address.substring(0,3).equals("+62")) {
                            address = address.substring(3, address.length());
                        } else if (address.substring(0,1).equals("0")) {
                            address = address.substring(1, address.length());
                        }
                        chatRoom.passNameWith(address, currentContact.getName());
                        movePage(currentContact.getName(), chatRoom);
                    }

                }
            });

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

    public void fetchContPhone() {

        wholeContUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phones = getActivity().getContentResolver().query(wholeContUri, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );

        if (phones.moveToFirst()) {
            do {

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String pnumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (!pnumb.contains("-")) {
                    Contacts.add(new Contact(1, name, pnumb, null));
                }

            } while (phones.moveToNext());
        }
        phones.close();
    }

}
