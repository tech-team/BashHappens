package org.techteam.bashhappens.rest;

public enum OperationType {
    GET_POSTS,
    BASH_VOTE,
    IT_VOTE;

    public static OperationType toType(int status) {
        switch (status) {
            case 0:
                return GET_POSTS;
            case 1:
                return BASH_VOTE;
            default:
                return IT_VOTE;
        }
    }

    public int toInt() {
        if (this == GET_POSTS) {
            return 0;
        } else if (this == BASH_VOTE) {
            return 1;
        } else {
            return 2;
        }
    }
}
