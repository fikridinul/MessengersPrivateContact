package com.thesisproject.fikri.messengers.messages;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.databases.DatabaseHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fikri on 20/03/2016.
 */
public class SmsListener extends BroadcastReceiver {

    String messageBody, nameC, param, incom;
    DatabaseHandler dbHandler;
    List<ChatMessage> chatMessages = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            dbHandler = new DatabaseHandler(context);
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                incom = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    messageBody = smsMessage.getMessageBody();
                    incom = smsMessage.getOriginatingAddress();
                    ContentValues values = new ContentValues();
                    values.put(Telephony.Sms.ADDRESS, incom);
                    values.put(Telephony.Sms.BODY, messageBody);
                    if (incom.substring(0,3).equals("+62")) {
                        incom = incom.substring(3, incom.length());
                    } else if (incom.substring(0,1).equals("0")) {
                        incom = incom.substring(1, incom.length());
                    }
                    if (checkContact(incom)) {
                        Contact indif = dbHandler.getContact(incom); //TODO-// FIXME: 14/04/2016 recently adited
                        sendMessage(messageBody, "1", indif.getName(), incom);
                    } else {
                        context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
                        soundNotification(context);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private void sendMessage(final String messageText, String userType, String with, String number) {
        if (messageText.trim().length()==0)
            return;

        final ChatMessage message =  new ChatMessage(1, with, userType, messageText, "SENT", new Date().getTime(), number);
        chatMessages.add(message);
        dbHandler.insertMessageChat(message);

    }

    private Boolean checkContact(String number) {
        //phone = number;

        int contactCount = dbHandler.getAllContacts().size();

        for (int i = 0; i < contactCount; i++) {
            if (number.compareToIgnoreCase(dbHandler.getAllContacts().get(i).getPhone()) == 0) {
                nameC = dbHandler.getAllContacts().get(i).getName();
                param = nameC;
                return true;
            }
        }
        return false;
    }

    public void soundNotification(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
