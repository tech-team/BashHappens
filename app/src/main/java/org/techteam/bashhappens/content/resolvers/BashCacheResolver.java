package org.techteam.bashhappens.content.resolvers;

import android.net.Uri;

import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashCache;

public class BashCacheResolver extends BashResolver {

    @Override
    protected Uri _getUri() {
        return Uri.parse(BashHappensDbProvider.CONTENT_URI + "/" + BashCache.TABLE_NAME);
    }
}
