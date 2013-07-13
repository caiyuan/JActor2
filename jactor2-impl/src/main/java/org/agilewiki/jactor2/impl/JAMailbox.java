package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Actor;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.ResponseProcessor;
import org.agilewiki.jactor2.api._Request;

import java.util.Queue;

/**
 * The extended Mailbox interface for use in the implementation.
 */
public interface JAMailbox extends Mailbox, AutoCloseable, MessageSource, Runnable {

    /**
     * Adds messages directly to the queue.
     *
     * @param messages Previously buffered messages.
     */
    void unbufferedAddMessages(final Queue<Message> messages) throws Exception;

    /**
     * Add a message directly to the queue.
     *
     * @param message A message.
     * @param local   True when the current thread is bound to the mailbox.
     */
    void unbufferedAddMessages(final Message message, final boolean local)
            throws Exception;

    <E, A extends Actor> Message createMessage(final boolean _foreign,
                                               final MessageQueue _inbox,
                                               final _Request<E, A> _request,
                                               final A _targetActor,
                                               final ResponseProcessor<E> _responseProcessor);

    /**
     * Returns true, if this mailbox is currently processing messages.
     */
    boolean isRunning();
}
