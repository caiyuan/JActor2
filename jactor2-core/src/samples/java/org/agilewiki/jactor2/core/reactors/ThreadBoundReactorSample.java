package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.ThreadBoundBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class ThreadBoundReactorSample {

    public static void main(String[] args) throws Exception {

        //A plant with no threads.
        final Plant plant = new Plant(0);

        //Get a reference to the main thread.
        final Thread mainThread = Thread.currentThread();

        //Create a thread-bound processing.
        final ThreadBoundReactor reactor =
                new ThreadBoundReactor(plant, new Runnable() {
                    @Override
                    public void run() {
                        //Interrupt the main thread when there are messages to process
                        mainThread.interrupt();
                    }
                });

        //Create an blades that uses the thread-bound processing.
        final SampleThreadBoundBlade threadBoundBlade = new SampleThreadBoundBlade(reactor);

        //Terminate the blades.
        new SyncRequest<Void>(threadBoundBlade.getReactor()) {

            @Override
            protected Void processSyncRequest() throws Exception {
                threadBoundBlade.fin();
                return null;
            }
        }.signal();

        //Process messages when this thread is interrupted.
        while (true) {
            try {
                //Wait for an interrupt.
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                //Process messages when the main thread is interrupted.
                reactor.run();
            }
        }
    }
}

class SampleThreadBoundBlade extends ThreadBoundBladeBase {

    SampleThreadBoundBlade(final ThreadBoundReactor _reactor) throws Exception {
        super(_reactor);
    }

    //Print "finished" and exit when fin is called.
    void fin() throws Exception {
        System.out.println("finished");
        System.exit(0);
    }
}
