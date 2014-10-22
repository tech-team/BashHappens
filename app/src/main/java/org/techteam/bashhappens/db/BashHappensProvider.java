package org.techteam.bashhappens.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class BashHappensProvider extends ContentProvider {

    private DatabaseHelper database;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public synchronized int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
