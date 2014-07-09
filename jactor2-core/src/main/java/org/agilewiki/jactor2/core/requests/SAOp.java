package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * Single-use asynchronous operation.
 */
public abstract class SAOp<RESPONSE_TYPE> extends AOp<RESPONSE_TYPE> implements AsyncResponseProcessor<RESPONSE_TYPE> {
    private AsyncRequestImpl asyncRequestImpl;
    private AsyncResponseProcessor<RESPONSE_TYPE> asyncResponseProcessor;

    public SAOp(String _opName, Reactor _targetReactor) {
        super(_opName, _targetReactor);
    }

    @Override
    final public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                      AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        if (asyncRequestImpl != null)
            throw new IllegalStateException("not reusable");
        asyncRequestImpl = _asyncRequestImpl;
        asyncResponseProcessor = _asyncResponseProcessor;
        processAsyncOperation(_asyncRequestImpl);
    }

    protected AsyncRequestImpl getAsyncRequestImpl() {
        return asyncRequestImpl;
    }

    protected AsyncResponseProcessor getAsyncResponseProcessor() {
        return asyncResponseProcessor;
    }

    @Override
    public void processAsyncResponse(RESPONSE_TYPE _response) throws Exception {
        asyncResponseProcessor.processAsyncResponse(_response);
    }

    abstract protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl)
            throws Exception;
}
