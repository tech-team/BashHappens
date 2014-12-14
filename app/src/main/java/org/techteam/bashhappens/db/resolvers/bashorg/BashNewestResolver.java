package org.techteam.bashhappens.db.resolvers.bashorg;

import android.net.Uri;

import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashNewest;

public class BashNewestResolver extends BashResolver {

    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashNewest.TABLE_NAME);
    }
}
