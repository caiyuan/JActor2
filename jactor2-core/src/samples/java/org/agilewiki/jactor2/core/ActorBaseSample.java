package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.mailbox.Mailbox;

public class ActorBaseSample extends ActorBase {
    public ActorBaseSample(final Mailbox _Mailbox) throws Exception {
        initialize(_Mailbox);
    }
}
