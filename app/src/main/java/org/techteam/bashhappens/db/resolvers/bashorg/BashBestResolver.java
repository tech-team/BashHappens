package org.techteam.bashhappens.db.resolvers.bashorg;

import android.net.Uri;

import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashBest;

public class BashBestResolver extends BashResolver {

    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashBest.TABLE_NAME);
    }
}
