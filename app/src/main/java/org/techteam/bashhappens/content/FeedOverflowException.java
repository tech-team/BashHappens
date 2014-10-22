package org.techteam.bashhappens.content;

public class FeedOverflowException extends Exception {
    public FeedOverflowException() {
    }

    public FeedOverflowException(String detailMessage) {
        super(detailMessage);
    }

    public FeedOverflowException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FeedOverflowException(Throwable throwable) {
        super(throwable);
    }
}
