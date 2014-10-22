package org.techteam.bashhappens.db.tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import static org.techteam.bashhappens.db.DataTypes.*;

public class ItLikes implements BaseColumns {
    public static final String TABLE_NAME = "it_likes";
    public static final String ARTICLE_ID = "article_id";
    public static final String IS_LIKED = "is_liked";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + ItLikes.TABLE_NAME + "(" + COLUMN_ID + SERIAL
            + ItLikes.ARTICLE_ID + INTEGER + SEPARATOR
            + ItLikes.IS_LIKED + BOOLEAN + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ItLikes.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
