package com.thesisproject.fikri.messengers.contacts;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;
import com.thesisproject.fikri.messengers.mainfragments.AddFragment;
import com.thesisproject.fikri.messengers.mainfragments.homefragments.HomeFragment;

public class ContactDetails extends Fragment {

    int idCont;
    Intent intent;
    Toolbar toolbar;
    String nameCont, numbCont;
    MainActivity mainActivity;
    DatabaseHandler dbHandler;
    EditText nameAcc, numbAcc;
    RelativeLayout relativeLayout;
    Fragment addFragment, homeFragment;
    FragmentTransaction fragmentTransaction;
    ImageView ivContDet, ivAppAcc, ivDiscApp;
    Uri uriCont, imageUri = Uri.parse("android.resource://com.thesisproject.fikri.messengers/drawable/" + R.drawable.ic_initial_acc);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_contact_details, container, false);
        mainActivity = new MainActivity();
        addFragment = new AddFragment();
        homeFragment = new HomeFragment();
        dbHandler = new DatabaseHandler(getContext());
        ivContDet = (ImageView) relativeLayout.findViewById(R.id.ivContactDet);
        ivAppAcc = (ImageView) relativeLayout.findViewById(R.id.ivAppAcc);
        ivDiscApp = (ImageView) relativeLayout.findViewById(R.id.ivDiscAcc);
        nameAcc = (EditText) relativeLayout.findViewById(R.id.editTextNameCont);
        numbAcc = (EditText) relativeLayout.findViewById(R.id.editTextPhoneCont);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        if (toolbar.getTitle() == "Edit Contact") {
            nameAcc.setText(nameCont);
            numbAcc.setText(numbCont);
            ivContDet.setImageURI(imageUri);
        }

        ivContDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent (Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory (Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        ivDiscApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameAcc.setText("");
                numbAcc.setText("");
                if (toolbar.getTitle() == "Add Contact") {
                    movePage("Add Contact", addFragment);
                } else {
                    movePage("Home", homeFragment);
                }
            }
        });

        ivAppAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = String.valueOf(numbAcc.getText());
                if (address.substring(0,3).equals("+62")) {
                    address = address.substring(3, address.length());
                } else if (address.substring(0,1).equals("0")) {
                    address = address.substring(1, address.length());
                }
                address = address.replaceAll("-", "").trim();

                if (toolbar.getTitle() == "Add Contact") {

                    Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nameAcc.getText()),
                           address , imageUri);
                    if (!contactExists(contact)) {
                        nameAcc.setText("");
                        numbAcc.setText("");
                        dbHandler.createContact(contact);
                        movePage("Home", homeFragment);
                    }
                } else {
                    Contact contact = new Contact(idCont, String.valueOf(nameAcc.getText()),
                            address, imageUri);
                    dbHandler.updateContact(contact, nameCont);
                    movePage("Home", homeFragment);
                }
            }
        });

        return relativeLayout;
    }

    @Override
    public void onActivityResult (int reqCode, int resCode, Intent data) {
        if (resCode == getActivity().RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                Log.d("Image", imageUri.toString());
                ivContDet.setImageURI(imageUri);
            }
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

    public void getContactDeatails (int id, String name, String phone, Uri imgUri) {
        idCont = id;
        nameCont = name;
        numbCont = "+62" + phone;
        imageUri = imgUri;
    }

}
