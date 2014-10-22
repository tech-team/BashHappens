package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import static org.techteam.bashhappens.db.DataTypes.*;

public class ItCache implements BaseColumns {
    public static final String TABLE_NAME = "it_cache";
    public static final String ID = "id";
    public static final String HEADER = "header";
    public static final String TEXT = "text";
    public static final String RATING = "rating";
    public static final String DATE = "date";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + ItCache.TABLE_NAME + "(" + COLUMN_ID + SERIAL
            + ItCache.ID + INTEGER + SEPARATOR
            + ItCache.HEADER + TEXT + SEPARATOR
            + ItCache.TEXT + TEXT + SEPARATOR
            + ItCache.RATING + INTEGER + SEPARATOR
            + ItCache.DATE + TIMESTAMP + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ItCache.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
