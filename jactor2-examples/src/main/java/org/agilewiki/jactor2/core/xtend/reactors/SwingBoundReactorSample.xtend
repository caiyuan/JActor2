package org.agilewiki.jactor2.core.xtend.reactors;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.agilewiki.jactor2.core.blades.SwingBoundBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

class SwingBoundReactorSample {
    def static void main(String[] _args) throws Exception {
        //Create a plant with 5 threads.
        val plant = new Plant(5);

        new HelloWorld(new SwingBoundReactor()).createAndShowAReq().signal();
    }
}

class HelloWorld extends SwingBoundBladeBase {
    new(SwingBoundReactor _reactor) throws Exception {
        super(_reactor);
    }

    def AsyncRequest<Void> createAndShowAReq() {
        return new AsyncRequest<Void>(this) {
            override void processAsyncRequest() {
                //Create and set up the window.
                val frame = new JFrame("HelloWorld");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //no exit until all threads are closed.

                //Close plant when window is closed.
                frame.addWindowListener(getReactor());

                //Add the "Hello World!" label.
                val label = new JLabel("Hello World!");
                frame.getContentPane().add(label);

                //Display the window.
                frame.pack();
                frame.setVisible(true);

                //return the result.
                processAsyncResponse(null);
            }
        };
    }

}
