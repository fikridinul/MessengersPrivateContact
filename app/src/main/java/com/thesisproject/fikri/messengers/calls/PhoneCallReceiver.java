package com.thesisproject.fikri.messengers.calls;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.*;
import android.util.Log;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.R;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhoneCallReceiver extends BroadcastReceiver {

    String formats, names, longs;
    Context contextt;
    String LOG_TAG = "State ";
    Call currentCall;
    int contactCount;
    Method methodEndCall;
    MainActivity main;
    Class classTelephony;
    String outgoingNumber;
    String incomingNumber;
    String state, phone, name;
    Object telephonyInterface;
    DatabaseHandler dbHandler;
    Method methodGetITelephony;
    Class telephonyInterfaceClass;
    TelephonyManager telephonyManager;
    Uri pathIn = Uri.parse("android.resource://com.thesisproject.fikri.messengers/drawable-hdpi/" + R.drawable.ic_call_received);
    Uri pathOut = Uri.parse("android.resource://com.thesisproject.fikri.messengers/drawable-hdpi/" + R.drawable.ic_call_made);

    //TODO-bug fixing: Find a way to handle state bug (called twice: try using date validation before, turn the ) and the outgoing call

    @Override
    public void onReceive(Context context, Intent intent) {
        this.contextt = context;
        dbHandler = new DatabaseHandler(context);
        main = new MainActivity();

        TelephonyManager mTM = (TelephonyManager) contextt.getSystemService(contextt.TELEPHONY_SERVICE);
        PhoneStateListeners stateListener =  new PhoneStateListeners(contextt, intent);
        mTM.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Incoming call
                if (incomingNumber.substring(0,3).equals("+62")) {
                    incomingNumber = incomingNumber.substring(3, incomingNumber.length());
                } else if (incomingNumber.substring(0,1).equals("0")) {
                    incomingNumber = incomingNumber.substring(1, incomingNumber.length());
                }
                if (checkContact(incomingNumber)) {
                    //setResultData(null);
                    if (killCall(context)) {
                        formats = new SimpleDateFormat("HH:mm dd-MM").format(new Date());
                        longs = new SimpleDateFormat("HH:mm").format(new Date());
                        names = this.gettingName(incomingNumber);
                        currentCall = new Call(dbHandler.getCallsCount(), names, incomingNumber, String.valueOf(formats), longs, pathIn);
                        dbHandler.createCall(currentCall);
                    }
                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            }

        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if (outgoingNumber.substring(0,3).equals("+62")) {
                outgoingNumber = outgoingNumber.substring(3, outgoingNumber.length());
            } else if (outgoingNumber.substring(0,1).equals("0")) {
                outgoingNumber = outgoingNumber.substring(1, outgoingNumber.length());
            }
            if (outgoingNumber.equals(dbHandler.getPass())) {
                setResultData(null);

                MainActivity.HIDE_MODE = false;

                context.getPackageManager().setComponentEnabledSetting(
                        new ComponentName(context, "com.thesisproject.fikri.messengers.MainActivity_Alias"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

//                context.getPackageManager().setComponentEnabledSetting(
//                        new ComponentName(context, "com.thesisproject.fikri.messengers.MainActivity"),
//                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                Intent appIntent = new Intent();
                appIntent.setComponent(new ComponentName(contextt, "com.thesisproject.fikri.messengers.MainActivity_Alias"));
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contextt.startActivity(appIntent);

            } else if (checkContact(outgoingNumber)) {
                formats = new SimpleDateFormat("HH:mm dd-MM").format(new Date());
                longs = new SimpleDateFormat("HH:mm").format(new Date());
                names = gettingName(outgoingNumber);
                currentCall = new Call(dbHandler.getCallsCount(), names, outgoingNumber, String.valueOf(formats), longs, pathOut);
                dbHandler.createCall(currentCall);
            }

        } else {

        }

    }

    public boolean killCall (Context context) {
        try {
            // Get the boring old TelephonyManager
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getTelephony() method
            classTelephony = Class.forName(telephonyManager.getClass().getName());
            methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get ITelephony interface
            telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean checkContact(String number) {
        //phone = number;
        contactCount = dbHandler.getAllContacts().size();

        for (int i = 0; i < contactCount; i++) {
            if (number.compareToIgnoreCase(dbHandler.getAllContacts().get(i).getPhone()) == 0) {
                return true;
            }
        }
        return false;
    }

    private String gettingName(String number) {
        phone = number;
        contactCount = dbHandler.getAllContacts().size();
        for (int i = 0; i < contactCount; i++) {
            String nope = dbHandler.getAllContacts().get(i).getPhone();
            if (nope.equalsIgnoreCase(phone)) {
                name = dbHandler.getAllContacts().get(i).getName();
            }
        }
        return name;
    }

}

class PhoneStateListeners extends PhoneStateListener{
    String LOG_TAG = "State ";
    Context context;
    int contactCount;
    DatabaseHandler dbHandler;
    String outgoingNumber;

    public PhoneStateListeners(Context contextt, Intent intent) {
        context = contextt;
        outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        //dbHandler = new DatabaseHandler(context);
        //if (!outgoingNumber.equals(dbHandler.getPass())) {
        //    if (checkContact(outgoingNumber)) {
                if (TelephonyManager.CALL_STATE_IDLE == state) {
                    //when this state occurs, and your flag is set, restart your app
                    delLogPhone(outgoingNumber);
                }
        //    }
        //}
    }

    public void delLogPhone(String outgoingNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            try {
                String strNumberOne[] = { outgoingNumber};
                Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", strNumberOne, "");
                boolean bol = cursor.moveToFirst();
                if (bol) {
                    do {
                        int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                        context.getContentResolver().delete(Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, String.valueOf(idOfRowToDelete)), "", null);
                    } while (cursor.moveToNext());
                }
            } catch (Exception ex) {
                System.out.print("Exception here ");
            }
            return;
        }
    }

//    public boolean checkContact(String number) {
//        //phone = number;
//        contactCount = dbHandler.getAllContacts().size();
//
//        for (int i = 0; i < contactCount; i++) {
//            if (number.compareToIgnoreCase(dbHandler.getAllContacts().get(i).getPhone()) == 0) {
//                return true;
//            }
//        }
//        return false;
//    }
}