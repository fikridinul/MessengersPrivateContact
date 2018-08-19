package com.thesisproject.fikri.messengers.databases;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import com.thesisproject.fikri.messengers.MainActivity;
import com.thesisproject.fikri.messengers.calls.Call;
import com.thesisproject.fikri.messengers.contacts.Contact;
import com.thesisproject.fikri.messengers.messages.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by fikri on 20/03/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    Context contextIn;

    private static final String DATABASE_NAME = "contactManager",
            TABLE_CONTACTS = "contacts",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PHONE = "phone",
            KEY_IMAGEURI = "imageUri",
            TABLE_CALLS = "callsLog",
            CALL_ID = "id",
            CALL_NAME = "name",
            CALL_PHONE = "phone",
            CALL_TIME = "time",
            CALL_LONGS = "longs",
            CALL_IMAGEURI = "imageUri",
            TABLE_MESSAGES = "messages",
            MESSAGE_ID = "id",
            MESSAGE_WITH = "name",
            MESSAGE_TYPE = "type",
            MESSAGE_CONTENT = "content",
            MESSAGE_STATUS = "status",
            MESSAGE_TIME = "time",
            MESSAGE_NUMBER = "number",
            TABLE_MULTIUSE = "multiUse",
            SINGLE_PASS = "password",
            SINGLE_PACK = "deffPack",
            SINGLE_MODE = "uiMode";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contextIn = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_IMAGEURI + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_CALLS + " (" + CALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CALL_NAME + " TEXT," + CALL_PHONE + " TEXT," + CALL_TIME + " TEXT,"
                + CALL_LONGS + " TEXT," + CALL_IMAGEURI + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " (" + MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MESSAGE_WITH + " TEXT, " + MESSAGE_TYPE + " TEXT, " + MESSAGE_CONTENT + " TEXT, "
                + MESSAGE_STATUS + " TEXT, " + MESSAGE_TIME + " INTEGER, " + MESSAGE_NUMBER + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MULTIUSE + " (" + SINGLE_PASS + " TEXT, " + SINGLE_PACK + " TEXT, " + SINGLE_MODE + " TEXT)");

        ContentValues values = new ContentValues();
        values.put(SINGLE_PASS, "##5555");
        values.put(SINGLE_PACK, MainActivity.def);
        values.put(SINGLE_MODE, "unhidden");
        db.insert(TABLE_MULTIUSE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MULTIUSE);
        onCreate(db);
    }

    //<-----------------------------------------------MULTI USE------------------------------------------->
    public void updateMode(String mode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SINGLE_MODE, mode);
        db.update(TABLE_MULTIUSE, values, null, null);
        db.close();
    }

    public boolean chekcHideMode(String check) {
        SQLiteDatabase db = getReadableDatabase();
        String get;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MULTIUSE, null);
        if (cursor.moveToFirst()) {
            get = cursor.getString(2);
            if (get.equals(check)) {
                return true;
            }
        }
        return false;
    }

    public String getDefaultApp() {
        String got = "";
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor =  db.rawQuery("SELECT * FROM " + TABLE_MULTIUSE, null);
        if (cursor.moveToFirst()) {
            got = cursor.getString(1);
        }
        cursor.close();
        db.close();
        return got;
    }

    public String getPass() {
        String got = "";
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MULTIUSE, null);
        if (cursor.moveToFirst()) {
            got = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return got;
    }

    //<-----------------------------------------------SETTINGS------------------------------------------->
    public void changePassword(String oldPass, String newPass) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SINGLE_PASS, newPass);

        db.update(TABLE_MULTIUSE, values, SINGLE_PASS + "=?", new String[]{oldPass});
        db.close();
    }

    //<-----------------------------------------------CONTACTS------------------------------------------->
    public void createContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public Contact getContact(String number) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE " + KEY_PHONE + " = ? ", new String[]{number});

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), Uri.parse(cursor.getString(3)));

        cursor.close();
        db.close();
        return contact;
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_NAME + "=?", new String[]{contact.getName()});
        db.close();
    }

    public int getContactsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public void updateContact(Contact contact, String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, contact.getId());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());
        db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});

        values.clear();
        values.put(MESSAGE_WITH, contact.getName());
        values.put(MESSAGE_NUMBER, contact.getPhone());
        db.update(TABLE_MESSAGES, values, MESSAGE_WITH + "=?", new String[]{name});

        values.clear();
        values.put(CALL_NAME, contact.getName());
        values.put(CALL_PHONE, contact.getPhone());
        db.update(TABLE_CALLS, values, CALL_NAME + "=?", new String[]{name});
        db.close();

    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        if (cursor.moveToFirst()) {
            do {
                contacts.add(new Contact(cursor.getInt(0), cursor.getString(1), cursor.getString(2), Uri.parse(cursor.getString(3))));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }

    //<-----------------------------------------------CALLS------------------------------------------->
    public void createCall(Call calls) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        String ident = calls.getLong();
        int check = checkCalls(ident);
        if (check == 0) {
            values.put(CALL_NAME, calls.getName());
            values.put(CALL_PHONE, calls.getPhone());
            values.put(CALL_TIME, calls.getTime());
            values.put(CALL_LONGS, ident);
            values.put(CALL_IMAGEURI, calls.getImageUri().toString());

            db.insert(TABLE_CALLS, null, values);
            db.close();
        }
    }

    public void deleteCall(String idORCall, String option) {
        SQLiteDatabase db = getWritableDatabase();
        if (option.equals("id")) {
            db.delete(TABLE_CALLS, CALL_ID + "=?", new String[]{idORCall});
        } else {
            db.delete(TABLE_CALLS, CALL_NAME + "=?", new String[]{idORCall});
        }
        db.close();
    }

    public int getCallsCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CALLS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public List<Call> getAllCalls() {
        List<Call> calls = new ArrayList<Call>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CALLS, null);

        if (cursor.moveToLast()) {
            do {
                calls.add(new Call(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5))));
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        db.close();
        return calls;
    }

    public Call getCall(String number) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CALLS, new String[]{CALL_ID, CALL_NAME, CALL_PHONE,
                CALL_TIME, CALL_LONGS, CALL_IMAGEURI,}, CALL_PHONE + new String[]{number}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Call call = new Call(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();
        return call;
    }

    public int checkCalls(String longes) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CALLS + " WHERE " + CALL_LONGS + " = ?", new String[]{longes});
        int many = cursor.getCount();
        cursor.close();
        return many;
    }

    //<-----------------------------------------------MESSAGES------------------------------------------->
    public List<ChatMessage> getAllChat (String with) {
        SQLiteDatabase db = getReadableDatabase();
        List<ChatMessage> message = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE " + MESSAGE_NUMBER + " = ? ", new String[]{with});
        if (cursor.moveToFirst()) {
            do {
                message.add(new ChatMessage(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getString(6)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return message;
    }

    public List<ChatMessage> checkAndGetLastChat () {
        SQLiteDatabase db =  getReadableDatabase();
        int many, itung, countlist;
        List<ChatMessage> popLastM = new ArrayList<>();
        ArrayList<String> counts, counts1;
        counts = new ArrayList<>();
        counts1=  new ArrayList<>();
        Cursor cursor,cursor1;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES, null);

        if (cursor.moveToLast()) {
            do {
                counts.add(cursor.getString(6));
            } while (cursor.moveToPrevious());
        }

        many = counts.size();

        for (int i=0;i<many;i++) {
            itung = 0;
            if (i==0){
                counts1.add(counts.get(i));
                itung += 1;
            } else {
                for (int j=0;j<counts1.size();j++) {
                    if (counts1.get(j).equals(counts.get(i))) {
                        itung += 1;
                    }
                }
            }
            if (itung==0) {
                counts1.add(counts.get(i));
            }
        }

        countlist = counts1.size();

        for (int k=0;k<countlist;k++) {
            cursor1 = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE " + MESSAGE_NUMBER + " = ? ", new String[] {counts1.get(k)});
            cursor1.moveToLast();
            popLastM.add(new ChatMessage(cursor1.getInt(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), cursor1.getString(4), cursor1.getLong(5), cursor1.getString(6)));
            cursor1.close();
        }


        cursor.close();
        db.close();

        return popLastM;
    }

    public void deleteMessage(String idORwith, String flag){
        SQLiteDatabase db = getWritableDatabase();
        if (flag.equals("id")) {
            db.delete(TABLE_MESSAGES, MESSAGE_ID + "=?", new String[]{idORwith});
        } else {
            db.delete(TABLE_MESSAGES, MESSAGE_NUMBER + "=?", new String[]{idORwith});
        }
        db.close();
    }

    public void insertMessageChat(ChatMessage message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MESSAGE_WITH, message.getMessageWith());
        values.put(MESSAGE_TYPE, message.getUserType());
        values.put(MESSAGE_CONTENT, message.getMessageText());
        values.put(MESSAGE_STATUS, message.getMessageStatus());
        values.put(MESSAGE_TIME, message.getMessageTime());
        values.put(MESSAGE_NUMBER, message.getMessageNumber());

        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public List<ChatMessage> fetchAllMessage() {
        List<ChatMessage> chat = new ArrayList<>();
        List<Contact> splitting = new ArrayList<>();
        Cursor c;
        String ids, thread_ids, addresss, dates,  reads, statuss, types, subjects, bodys, seens, person;

        Uri allMessage = Telephony.Sms.CONTENT_URI;//Uri.parse("content://sms/");
        ContentResolver cr = contextIn.getContentResolver();
        c = cr.query(allMessage, new String[]{"_id", "address", "date", "read", "status", "type", "subject", "body", "seen"}, null, null, null);
        String[] columns = new String[] {"_id", "address", "date", "read", "status", "type", "subject", "body", "seen"};
        try {
            if (c.moveToFirst()) {
                do {
                    ids = c.getString(c.getColumnIndex(columns[0]));
                    addresss = c.getString(c.getColumnIndex(columns[1])).replaceAll("-", "").replaceAll("\\s", "");
                    dates = c.getString(c.getColumnIndex(columns[2]));
                    //reads = c.getString(c.getColumnIndex(columns[3]));
                    //statuss = c.getString(c.getColumnIndex(columns[4]));
                    types = c.getString(c.getColumnIndex(columns[5]));
                    //subjects = c.getString(c.getColumnIndex(columns[6]));
                    bodys = c.getString(c.getColumnIndex(columns[7]));
                    //seens = c.getString(c.getColumnIndex(columns[8]));

                    person = fetchContPhone(addresss);
//                    splitting = fetchContPhone1();
//                    person = addresss;
//                    for (int o=splitting.size()-1;o>=0;o--) {
//                        if (splitting.get(o).getPhone().equals(addresss)) {
//                            person = splitting.get(o).getName();
//                        }
//                    }

                    if (addresss.substring(0, 3).equals("+62")) {
                        addresss = addresss.substring(3, addresss.length());
                    } else if (addresss.substring(0, 1).equals("0")) {
                        addresss = addresss.substring(1, addresss.length());
                    }

                    chat.add(new ChatMessage(Integer.parseInt(ids), person, types, bodys, types, Long.parseLong(dates), addresss));

                } while (c.moveToNext());
            }
        } catch (Exception e) {

        }
        return chat;
    }

    public List<Contact> fetchContPhone1() {
        List<Contact> Contacts = new ArrayList<>();
        Uri wholeContUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phones = contextIn.getContentResolver().query(wholeContUri, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );

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
        return Contacts;
    }

    public String fetchContPhone(String args) {
        List<Contact> Contacts = new ArrayList<>();
        Uri wholeContUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phones = contextIn.getContentResolver().query(wholeContUri, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] {args}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );
        String precise = args;

        if (phones.moveToFirst()) {
            do {

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //String pnumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                precise = name;

            } while (phones.moveToNext());
        }
        phones.close();
        return precise;
    }

    public List<ChatMessage> fetchMessage(String with) {
        List<ChatMessage> chat = new ArrayList<>();
        List<ChatMessage> allInOne = new ArrayList<>();
        int mCount;

        /*String ids, thread_ids, addresss,
                persons, dates, protocols, reads, statuss, types,
                reply_path_presents, subjects, bodys, service_centers,
                lockeds, error_codes, seens, creators;

        Uri allMessage = Telephony.Sms.CONTENT_URI;//Uri.parse("content://sms/");
        ContentResolver cr = contextIn.getContentResolver();
        Cursor c = cr.query(allMessage, new String[]{"_id", "address",
                        "person", "date", "read", "status", "type", "reply_path_present", "body", "seen", "creator"},
                "address = ? ", new String[] {with}, null);
        String[] columns = new String[] {"_id", "address",
                "person", "date", "read", "status", "type",
                "reply_path_present", "body", "seen", "creator"};
        if (c.moveToLast()) {
            do {
                ids = c.getString(c.getColumnIndex(columns[0]));
                addresss = c.getString(c.getColumnIndex(columns[1])).replaceAll("-", "").trim();
                persons = c.getString(c.getColumnIndex(columns[2]));
                dates = c.getString(c.getColumnIndex(columns[3]));
                reads = c.getString(c.getColumnIndex(columns[4]));
                statuss = c.getString(c.getColumnIndex(columns[5]));
                types = c.getString(c.getColumnIndex(columns[6]));
                reply_path_presents = c.getString(c.getColumnIndex(columns[7]));
                bodys = c.getString(c.getColumnIndex(columns[8]));
                seens = c.getString(c.getColumnIndex(columns[9]));
                creators = c.getString(c.getColumnIndex(columns[10]));

                chat.add(new ChatMessage(Integer.parseInt(ids), addresss, types, bodys, "SENT", Long.parseLong(dates), addresss));
            } while (c.moveToPrevious());
        }
        c.close();*/

        allInOne = fetchAllMessage();
        mCount = allInOne.size();

        for (int i=mCount-1;i>=0;i--) {
            if (allInOne.get(i).getMessageNumber().equals(with)) {
                chat.add(allInOne.get(i));
            }
        }

        return chat;
    }

    public List<ChatMessage> checkAndGetLastChatProvider () {
        SQLiteDatabase db =  getReadableDatabase();
        int many, itung, countlist;
        List<ChatMessage> popLastM = new ArrayList<>();
        List<ChatMessage> allInOne = new ArrayList<>();
        List<ChatMessage> counts, counts1;
        counts = new ArrayList<>();
        counts1=  new ArrayList<>();
        Cursor c,c1;

        /*Uri allMessage = Telephony.Sms.CONTENT_URI;//Uri.parse("content://sms/");
        ContentResolver cr = contextIn.getContentResolver();
        c = cr.query(allMessage, new String[]{"_id", "address",
                        "person", "date", "read", "status", "type", "reply_path_present", "body", "seen"},
                null, null, null);
        String[] columns = new String[] {"_id", "address",
                "person", "date", "read", "status", "type",
                "reply_path_present", "body", "seen"};


        if (c.moveToFirst()) {
            do {
                counts.add(c.getString(c.getColumnIndex(columns[1])).replaceAll("-", "").trim());
            } while (c.moveToNext());
        }

        many = counts.size();

        for (int i=0;i<many;i++) {
            itung = 0;
            if (i==0){
                counts1.add(counts.get(i));
                itung += 1;
            } else {
                for (int j=0;j<counts1.size();j++) {
                    if (counts1.get(j).equals(counts.get(i))) {
                        itung += 1;
                    }
                }
            }
            if (itung==0) {
                counts1.add(counts.get(i));
            }
        }

        countlist = counts1.size();

        for (int k=0;k<countlist;k++) {
            try {
                c1 = cr.query(allMessage, new String[]{"_id", "address",
                                "person", "date", "read", "status", "type", "reply_path_present", "body", "seen"},
                        "address = ? ", new String[]{counts1.get(k)}, null);
                String[] columns1 = new String[]{"_id", "address",
                        "person", "date", "read", "status", "type",
                        "reply_path_present", "body", "seen"};
                c1.moveToFirst();
                popLastM.add(new ChatMessage(Integer.parseInt(c1.getString(c.getColumnIndex(columns1[0]))), c1.getString(c.getColumnIndex(columns1[1])).replaceAll("-", "").trim(),
                        c1.getString(c.getColumnIndex(columns1[6])), c1.getString(c.getColumnIndex(columns1[8])), "SENT", Long.parseLong(c1.getString(c.getColumnIndex(columns1[3]))), c1.getString(c.getColumnIndex(columns1[1])).replaceAll("-", "").trim()));
                c1.close();
            } catch (Exception e) {

            }
        }


        c.close();
        db.close();*/

        counts = fetchAllMessage();
        many = counts.size();

        for (int i=0;i<many;i++) {
            itung = 0;
            if (i == 0) {
                counts1.add(counts.get(i));
                itung += 1;
            } else {
                for (int j = 0; j < counts1.size(); j++) {
                    if (counts1.get(j).getMessageNumber().equals(counts.get(i).getMessageNumber())) {
                        itung += 1;
                    }
                }
            }
            if (itung == 0) {
                counts1.add(counts.get(i));
            }
        }

        //return popLastM;
        return counts1;
    }
}
