package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testa() throws Exception {
        System.out.println("testa");
        final Plant plant = new Plant();
        final NonBlockingReactor reactor = new NonBlockingReactor(plant);
        final Blade1 blade1 = new Blade1(reactor);
        final Blade2 blade2 = new Blade2(reactor);
        final String result = blade2.hi2AReq(blade1).call();
        assertEquals("Hello world!", result);
        plant.close();
    }
}
