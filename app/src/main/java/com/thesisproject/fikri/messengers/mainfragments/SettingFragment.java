package com.thesisproject.fikri.messengers.mainfragments;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;

import java.util.regex.Pattern;

public class SettingFragment extends Fragment {

    Switch switchDef;
    Button settingButton1;
    DatabaseHandler dbHandler;
    EditText settingEditText1;
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_setting, container, false);
        settingButton1 = (Button) relativeLayout.findViewById(R.id.settingButton1);
        settingEditText1 = (EditText) relativeLayout.findViewById(R.id.settingEditText1);
        switchDef = (Switch) relativeLayout.findViewById(R.id.switchDef);
        dbHandler = new DatabaseHandler(getActivity());

        settingButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePass(settingEditText1.getText().toString())) {
                    dbHandler.changePassword(dbHandler.getPass(), settingEditText1.getText().toString());
                }
            }
        });

        switchDef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            //TODO-// FIXME: 11/04/2016 make sure to fix this
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String pack = getActivity().getApplicationContext().getPackageName();
                if (switchDef.isChecked()) {
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,pack);
                    startActivity(intent);
                } else {
                    Intent intents = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intents.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, dbHandler.getDefaultApp());
                    startActivity(intents);
                }

                if(Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(getActivity().getPackageName())) {
                    switchDef.setChecked(true);
                } else {
                    switchDef.setChecked(false);
                }
            }
        });

        return relativeLayout;
    }

    public boolean validatePass(String pass) {
        if (pass.length() == 6) {
            if (pass.substring(0, 2).equals("##")) {
                if (Pattern.compile("[0-9]+").matcher(pass.substring(2,6)).matches()) {
                    if (!dbHandler.getPass().equals(pass)) {
                        Toast.makeText(getActivity(), "You have successfully change the password!", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), "You currently use the same password!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Make sure to enter number in the last four character!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Make sure to enter ## in the first two character!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Make sure to fill the input completely!", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}
