package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Actor;
import org.agilewiki.jactor2.api.ResponseProcessor;
import org.agilewiki.jactor2.api._Request;

/**
 * A source of requests, which must be able to handle a response.
 */
public interface MessageSource {

    /**
     * Process an incoming response.
     */
    void incomingResponse(final Message message, final JAMailbox responseSource);

    /**
     * Returns true, if the message was buffered for sending later.
     *
     * @param message Message to send-buffer
     * @param target  The MessageSource that should eventually receive this message
     * @return true, if buffered
     */
    boolean buffer(final Message message, final JAMailbox target);
}
