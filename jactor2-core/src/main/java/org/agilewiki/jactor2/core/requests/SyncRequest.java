package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.impl.plantImpl.PlantImpl;
import org.agilewiki.jactor2.core.impl.requestsImpl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;

/**
 * A sync request performs an operation safely within the thread context of the target reactor.
 *
 * @param <RESPONSE_TYPE> The type of response value.
 */
abstract public class SyncRequest<RESPONSE_TYPE> implements Request<RESPONSE_TYPE> {

    private final RequestImpl<RESPONSE_TYPE> requestImpl;

    /**
     * Create a SyncRequest.
     *
     * @param _targetReactor The targetReactor where this SyncRequest object is passed for processing.
     *                       The thread owned by this targetReactor will process this SyncRequest.
     */
    public SyncRequest(final Reactor _targetReactor) {
        requestImpl = PlantImpl.getSingleton().createSyncRequestImpl(this, _targetReactor);
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @return The value returned by the target blades.
     */
    abstract public RESPONSE_TYPE processSyncRequest() throws Exception;

    @Override
    public RequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return requestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return requestImpl.getTargetReactor();
    }

    @Override
    public Reactor getSourceReactor() {
        return requestImpl.getSourceReactor();
    }

    @Override
    public void signal() {
        requestImpl.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return requestImpl.call();
    }

    @Override
    public boolean isCanceled() throws ReactorClosedException {
        return requestImpl.isCanceled();
    }
}
