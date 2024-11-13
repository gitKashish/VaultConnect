package com.example.privatecontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CONTACTS = "contacts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addContact(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                null, null, null, null, COLUMN_NAME);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public ArrayList<Contact> queryContacts(String query) {
        ArrayList<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                COLUMN_NAME + " LIKE ? OR " + COLUMN_PHONE + " LIKE ?", new String[]{"%"+query+"%", "%"+query+"%"}, null, null, COLUMN_NAME);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public boolean checkDuplicateContact(String contactName, String contactPhone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CONTACTS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                COLUMN_NAME + "=? AND " + COLUMN_PHONE + "=?",
                new String[]{contactName, contactPhone},
                null, null, null);

        boolean isDuplicate = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isDuplicate;
    }

    public void updateContact(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Contact getContactById(int id) {
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CONTACTS,
                new String[] {COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            Contact contact = new Contact();
            contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            contact.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            cursor.close();
            db.close();
            return contact;
        } else {
            return null;
        }

    }
}

