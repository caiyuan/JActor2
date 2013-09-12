package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class BladeE {
    private final Reactor reactor;

    public BladeE(final Facility _facility) {
        this.reactor = new IsolationReactor(_facility);
    }

    public AsyncRequest<Void> throwRequest(final BladeA bladeA) {
        return new AsyncRequest<Void>(reactor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                // Note: we only respond to responseProcessor if we get a
                // response to our own request, which should NOT happen.
                // Therefore, responseProcessor is NOT called.
                try {
                    bladeA.throwRequest.send(messageProcessor,
                            new AsyncResponseProcessor<Void>() {

                                @Override
                                public void processAsyncResponse(final Void response)
                                        throws Exception {
                                    // Should NOT happen!
                                    dis.processAsyncException(new IllegalStateException(
                                            "We should have never got here!"));
                                }
                            });
                } catch (final Exception e) {
                    // Make sure we also don't throw anything, which would be
                    // passed to responseProcessor as a response ...
                    e.printStackTrace();
                }
            }
        };
    }
}