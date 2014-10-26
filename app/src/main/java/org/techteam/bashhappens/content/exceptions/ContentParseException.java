package org.techteam.bashhappens.content.exceptions;

public class ContentParseException extends Exception {

    public ContentParseException() {
    }

    public ContentParseException(String detailMessage) {
        super(detailMessage);
    }

    public ContentParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ContentParseException(Throwable throwable) {
        super(throwable);
    }
}
