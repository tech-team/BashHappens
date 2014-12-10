package org.techteam.bashhappens.rest;

import org.techteam.bashhappens.rest.service_helper.ServiceCallback;

import java.util.HashMap;
import java.util.Map;

public class CallbacksMaintainer {
    private Map<OperationType, ServiceCallback> callbacks = new HashMap<>();

    public CallbacksMaintainer() {
    }

    public CallbacksMaintainer addCallback(OperationType operationType, ServiceCallback cb) {
        callbacks.put(operationType, cb);
        return this;
    }

    public ServiceCallback getCallback(OperationType operationType) {
        return callbacks.get(operationType);
    }
}
