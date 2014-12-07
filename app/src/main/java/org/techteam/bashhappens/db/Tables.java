package org.techteam.bashhappens.db;

import org.techteam.bashhappens.db.tables.*;

public abstract class Tables {
    public static final ITable[] TABLES = new ITable[] {
                                       new BashNewest(),
                                       new BashLikes(),
                                       new BashBayan(),
                                       new BashFavs(),
                                       new ItNewest(),
                                       new ItLikes()
                                    };
}
