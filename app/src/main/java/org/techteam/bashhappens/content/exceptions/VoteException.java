package org.techteam.bashhappens.content.exceptions;

public class VoteException extends Exception {
    public VoteException() {
        super("Couldn't vote. Server says rude things");
    }

    public VoteException(String detailMessage) {
        super(detailMessage);
    }

    public VoteException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public VoteException(Throwable throwable) {
        super(throwable);
    }
}
