package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.techteam.bashhappens.db.DatabaseHelper;

public class BashFavs extends AbstractTable {

    public static final String TABLE_NAME = "bash_favs";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/bash_favs/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.bashfavs/org.techteam.bashhappens.db";

    public BashFavs() {}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable(TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        super.onUpgrade(database, oldVersion, newVersion, TABLE_NAME);
    }
}

