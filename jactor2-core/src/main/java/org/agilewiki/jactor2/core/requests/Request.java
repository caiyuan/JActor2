package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * A request is a single-use object for performing an operation safely and to optionally be passed back with a response
 * value that is also processed safely.
 *
 * @param <RESPONSE_TYPE>    The type response value.
 */
public interface Request<RESPONSE_TYPE> extends Op<RESPONSE_TYPE> {
    /**
     * Returns the object used to implement the request.
     *
     * @return The object used to implement the request.
     */
    RequestImpl<RESPONSE_TYPE> asRequestImpl();

    /**
     * Returns the Reactor that provides the thread context in which this request will operate.
     *
     * @return The target Reactor.
     */
    Reactor getTargetReactor();

    /**
     * Returns the source reactor, or null.
     * A null is returned if the request was passed using the signal or call methods.
     *
     * @return The source reactor or null.
     */
    Reactor getSourceReactor();

    /**
     * <p>
     * Returns true if the request has been canceled.
     * Closing the source reactor, for example, will result in the request being canceled.
     * This method should ideally be called periodically within long loops.
     * </p>
     * <p>
     * A check is also made to see if the request is closed. If processing continues for any length of time
     * after the request is closed, then a hung thread can result and a recovery process
     * is initiated. To avoid this, call isCanceled withing any loops. It will throw an
     * ReactorClosedException if the request is closed.
     * </p>
     *
     * @return True if the request has been canceled.
     * @throws ReactorClosedException Thrown when the request is closed.
     */
    boolean isCanceled() throws ReactorClosedException;
}
