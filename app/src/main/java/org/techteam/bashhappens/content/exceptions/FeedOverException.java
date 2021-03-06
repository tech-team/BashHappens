package org.techteam.bashhappens.content.exceptions;

public class FeedOverException extends Exception {
    public FeedOverException() {
        super("Feed is over");
    }

    public FeedOverException(String detailMessage) {
        super(detailMessage);
    }

    public FeedOverException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FeedOverException(Throwable throwable) {
        super(throwable);
    }
}
