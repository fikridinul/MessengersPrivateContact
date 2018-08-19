package com.thesisproject.fikri.messengers.mainfragments.homefragments;


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

import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.calls.Call;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeLogFrag extends Fragment {

    Toolbar toolbar;
    Call currentCall;
    ImageView ivCallImage;
    ListView callListView;
    FrameLayout frameLayout;
    DatabaseHandler dbHandler;
    TextView name, phone, time;
    ArrayAdapter<Call> callAdapter;
    FragmentTransaction fragmentTransaction;
    List<Call> Calls = new ArrayList<Call>();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        dbHandler = new DatabaseHandler(getActivity());
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_home_log, container, false);
        callListView = (ListView) frameLayout.findViewById(R.id.listViewLog);

        if (dbHandler.getCallsCount() != 0) {
            Calls.clear();
            Calls.addAll(dbHandler.getAllCalls());
        }

        populateList();

        callListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentCall = Calls.get(position);
                //TODO-assign: assign an action for this
                int idCall = currentCall.getId();
                dbHandler.deleteCall(String.valueOf(idCall), "id");
                Calls.remove(position);
                callAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return frameLayout;
    }

    private void populateList() {
        callAdapter =  new CallListAdapter();
        callListView.setAdapter(callAdapter);
    }

    private class CallListAdapter extends ArrayAdapter<Call> {
        public CallListAdapter() {
            super (getActivity(), R.layout.list_view_call_log, Calls);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.list_view_call_log, parent, false);

            currentCall = Calls.get(position);

            name = (TextView) view.findViewById(R.id.textViewNameCall);
            name.setText(currentCall.getName());
            phone = (TextView) view.findViewById(R.id.textViewNumbCall);
            phone.setText("+62" + currentCall.getPhone());
            time = (TextView) view.findViewById(R.id.textViewTimeCall);
            time.setText(currentCall.getTime());
            ivCallImage = (ImageView) view.findViewById(R.id.ivCallImage);
            ivCallImage.setImageURI(currentCall.getImageUri());
            return view;
        }
    }

    public void movePage(String namePage, Fragment nameFragment) {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nameFragment);
        fragmentTransaction.commit();

        toolbar.setTitle(namePage);
    }

}
