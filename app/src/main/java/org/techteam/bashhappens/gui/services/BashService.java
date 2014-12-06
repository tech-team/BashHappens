package org.techteam.bashhappens.gui.services;

import android.app.IntentService;

@Deprecated
public abstract class BashService extends IntentService {

    public static final class Urls {
        private static final String VOTE = "http://bash.im/quote/%s/%s";

        public static final String ACT_UP = "rulez";
        public static final String ACT_DOWN = "sux";
        public static final String ACT_BAYAN = "bayan";

        public static String getVoteUp(String id) {
            return String.format(VOTE, id, ACT_UP);
        }

        public static String getVoteDown(String id) {
            return String.format(VOTE, id, ACT_DOWN);
        }

        public static String getVoteBayan(String id) {
            return String.format(VOTE, id, ACT_BAYAN);
        }
    }

    public BashService(String name) {
        super(name);
    }
}
