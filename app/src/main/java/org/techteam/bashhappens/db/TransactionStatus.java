package org.techteam.bashhappens.db;

public enum TransactionStatus {
    STARTED,
    PROCESSING,
    FINISHED;

    public static TransactionStatus toStatus(int status) {
        if (status == 0) {
            return STARTED;
        } else if (status == 1) {
            return PROCESSING;
        } else {
            return FINISHED;
        }
    }

    public int toInt() {
        if (this == STARTED) {
            return 0;
        } else if (this == PROCESSING) {
            return 1;
        } else {
            return 2;
        }
    }
}
