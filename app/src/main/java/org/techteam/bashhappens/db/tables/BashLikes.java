package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.ITable;

import static org.techteam.bashhappens.db.DataTypes.*;

public class BashLikes implements BaseColumns, ITable {
    public static final String TABLE_NAME = "bash_likes";
    public static final String ARTICLE_ID = "article_id";
    public static final String DIRECTION = "direction";
    public static final String IS_BOYAN = "is_boyan";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/bash_likes/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.bashlikes/org.techteam.bashhappens.db";

    public BashLikes() {}

    private static final String TABLE_CREATE = "CREATE TABLE "
            + BashLikes.TABLE_NAME + "(" + COLUMN_ID + SERIAL
            + BashLikes.ARTICLE_ID + INTEGER + SEPARATOR
            + BashLikes.DIRECTION + INTEGER + SEPARATOR
            + BashLikes.IS_BOYAN + BOOLEAN + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BashLikes.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
