package org.techteam.bashhappens.content.bashorg;

public final class BashOrgUrls {
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
