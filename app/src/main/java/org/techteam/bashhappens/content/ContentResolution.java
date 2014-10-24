package org.techteam.bashhappens.content;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashCache;

public class ContentResolution {

    public static Cursor getBashCache(Activity act) {
        Uri uri = Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashCache.TABLE_NAME);
        return act.getContentResolver().query(uri,
                new String[]{BashCache._ID}, null, null, null);

    }
}
