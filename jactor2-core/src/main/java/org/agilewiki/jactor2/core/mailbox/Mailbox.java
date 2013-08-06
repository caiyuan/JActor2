package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.MessageSource;

/**
 * The Mailbox interface identifies the mailbox methods that can be used by applications.
 */
public interface Mailbox extends Runnable, MessageSource, AutoCloseable {

    /**
     * Returns the mailbox context.
     *
     * @return The mailbox context.
     */
    JAContext getJAContext();

    /**
     * Replace the current ExceptionHandler with another.
     * <p>
     * When an event or request message is processed by a mailbox, the current
     * exception handler is set to null. When a request is sent by a mailbox, the
     * current exception handler is saved in the outgoing message and restored when
     * the response message is processed.
     * </p>
     *
     * @param exceptionHandler The exception handler to be used now.
     *                         May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     *         default exception handler was in effect.
     */
    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);

    /**
     * Returns true when there are no more messages in the inbox. This method is generally
     * only called by a mailbox's onIdle task to determine when to return so that an
     * incoming message can be processed.
     *
     * @return True when the inbox is empty.
     */
    boolean isInboxEmpty();

    /**
     * Processes the messages in the inbox. For a thread-bound mailbox this method must
     * be called by the thread it is bound to, while for non-blocking and atomic mailboxes
     * this method is called by ThreadManager.
     */
    @Override
    void run();
}
