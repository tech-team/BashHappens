package org.techteam.bashhappens.db.tables;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.techteam.bashhappens.db.DatabaseHelper;

public class BashNewest extends AbstractTable {

    public static final String TABLE_NAME = "bash_newest";

    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + DatabaseHelper.AUTHORITY + "/bash_newest/");
    public static final String CONTENT_TYPE
            = "org.techteam.bashhappens.db.tables.bashnewest/org.techteam.bashhappens.db";

    public BashNewest() {}

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
