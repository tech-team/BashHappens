package org.techteam.bashhappens.db;

import android.database.sqlite.SQLiteDatabase;

public interface ITable {
    void onCreate(SQLiteDatabase db);
    void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);
}
