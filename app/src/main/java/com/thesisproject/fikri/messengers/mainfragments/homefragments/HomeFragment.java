package com.thesisproject.fikri.messengers.mainfragments.homefragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;
import com.thesisproject.fikri.messengers.mainfragments.SettingFragment;
import com.thesisproject.fikri.messengers.messages.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    Toolbar toolbar;
    public FloatingActionButton fab;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    FragmentTransaction fragmentTransaction;
    ListView contactListView;
    Contact currentContact;
    TextView name, phone;
    ImageView ivContactImage;
    ArrayAdapter<Contact> contactAdapter;
    DatabaseHandler dbHandler;
    RelativeLayout relativeLayout;
    ChatRoom chatRoom = new ChatRoom();
    List<Contact> Contacts = new ArrayList<>();
    Dialog dialog;

    MyAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_home, null);
        tabLayout = (TabLayout) relativeLayout.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) relativeLayout.findViewById(R.id.viewPager);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        dbHandler = new DatabaseHandler(getActivity());
        dialog = new Dialog(getActivity());
        myAdapter = new MyAdapter(getChildFragmentManager());

        if (dbHandler.chekcHideMode("hidden")) {
            myAdapter.addFrag(new HomeContactFrag(), "Contacts");
            myAdapter.addFrag(new HomeMessageFrag(), "Messages");
            myAdapter.addFrag(new HomeLogFrag(), "Calls");
        } else {
            myAdapter.addFrag(new HomeContactFrag(), "Contacts");
            myAdapter.addFrag(new HomeMessageFrag(), "Messages");
            fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (dbHandler.chekcHideMode("hidden")) {
                    if (position == 1) {
                        fab.show();
                    } else {
                        if (fab.isShown()) {
                            fab.hide();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(myAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return relativeLayout;
    }

    class MyAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (fab.isShown()) {
            fab.hide();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Home");
    }

    public void movePage(String namePage, Fragment nameFragment) {
        //Handle moving page and change toolbar title
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nameFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(namePage);
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

            currentContact = Contacts.get(position);

            name = (TextView) view.findViewById(R.id.textViewName);
            name.setText(currentContact.getName());
            phone = (TextView) view.findViewById(R.id.textViewPhone);
            phone.setText("+62" + currentContact.getPhone());
            ivContactImage = (ImageView) view.findViewById(R.id.ivContactIma);
            ivContactImage.setImageURI(currentContact.getImageURI());
            return view;
        }
    }

    public void showDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_home_contact, null);
        contactListView = (ListView) view.findViewById(R.id.listViewContact);
        Contacts.clear();
        Contacts = dbHandler.getAllContacts();

        populateList();
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentContact = Contacts.get(position);
                String names = currentContact.getName();
                String number = currentContact.getPhone();
                chatRoom.passNameWith(number, names);
                movePage(names, chatRoom);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setTitle("Contacts:");
        dialog.show();
    }

}
