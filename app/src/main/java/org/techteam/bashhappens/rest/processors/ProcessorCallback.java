package org.techteam.bashhappens.rest.processors;

public interface ProcessorCallback {
    void onSuccess();
    void onError(String message);
}
