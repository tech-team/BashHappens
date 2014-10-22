package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.security.Timestamp;

import static org.techteam.bashhappens.db.DataTypes.*;

public class BashCache implements BaseColumns {

    public static final String TABLE_NAME = "bash_cache";
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String RATING = "rating";
    public static final String DATE = "date";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + BashCache.TABLE_NAME + "(" + COLUMN_ID + SERIAL
            + BashCache.ID + INTEGER + SEPARATOR
            + BashCache.TEXT + TEXT + SEPARATOR
            + BashCache.RATING + INTEGER + SEPARATOR
            + BashCache.DATE + TIMESTAMP + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BashCache.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
