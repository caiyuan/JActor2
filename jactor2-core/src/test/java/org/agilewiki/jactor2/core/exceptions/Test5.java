package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test5 extends TestCase {
    public void testCascading() throws Exception {
        final Facility facility = new Facility();
        final ActorE actorE = new ActorE(facility);
        final Reactor reactorA = new IsolationReactor(facility);
        final ActorA actorA = new ActorA(reactorA);
        try {
            actorE.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            // It's magic! We get the SecurityException, although our request
            // did not throw it, or return it as response. This shows that
            // child request exceptions are passed up to the parent request.
            facility.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }
}
