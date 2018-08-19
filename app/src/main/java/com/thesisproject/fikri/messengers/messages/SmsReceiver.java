package com.thesisproject.fikri.messengers.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fikri on 20/03/2016.
 */
public class SmsReceiver extends BroadcastReceiver {

/*    String messageBody, nameC, param, incom;
    DatabaseHandler dbHandler;
    List<ChatMessage> chatMessages = new ArrayList<>();*/

    @Override
    public void onReceive(Context context, Intent intent) {
/*        try {
            dbHandler = new DatabaseHandler(context);
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                incom = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    messageBody = smsMessage.getMessageBody();
                    incom = smsMessage.getOriginatingAddress();
                    *//*ContentValues values =  new ContentValues();
                    values.put(Telephony.Sms.ADDRESS, incom);
                    values.put(Telephony.Sms.BODY, messageBody);
                    context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);*//*
                }
            }
        } catch (Exception e) {

        }*/
    }
 /*   private void sendMessage(final String messageText, String userType, String with) {
        if (messageText.trim().length()==0)
            return;

        final ChatMessage message =  new ChatMessage(1, with, userType, messageText, "SENT", new Date().getTime());
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
    }*/


}
