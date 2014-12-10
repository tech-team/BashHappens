package org.techteam.bashhappens.rest;

import android.os.Bundle;

public interface ServiceCallback {
    void onSuccess(String operationId, Bundle data); // TODO
    void onError(String operationId, Bundle data, String message); // TODO
}
