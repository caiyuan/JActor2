package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.Reactor;

public class BladeB {
    private final Reactor reactor;

    public BladeB(final Reactor mbox) {
        this.reactor = mbox;
    }

    public AsyncRequest<Void> throwRequest(final BladeA bladeA) {
        return new AsyncRequest<Void>(reactor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                bladeA.throwRequest.send(messageProcessor, this);
            }
        };
    }
}