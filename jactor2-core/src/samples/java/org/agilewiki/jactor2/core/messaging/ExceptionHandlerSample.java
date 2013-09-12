package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A facility with two threads.
        final Facility facility = new Facility(2);

        try {

            //Create an ExceptionActor.
            ExceptionActor exceptionActor = new ExceptionActor(new NonBlockingReactor(facility));

            try {
                //Create and call an exception request.
                exceptionActor.exceptionAReq().call();
                System.out.println("can not get here");
            } catch (IllegalStateException ise) {
                System.out.println("got first IllegalStateException, as expected");
            }

            //Create an ExceptionHandlerActor.
            ExceptionHandlerActor exceptionHandlerActor =
                    new ExceptionHandlerActor(exceptionActor, new NonBlockingReactor(facility));
            //Create a test request, call it and print the results.
            System.out.println(exceptionHandlerActor.testAReq().call());

        } finally {
            //shutdown the facility
            facility.close();
        }
    }
}

//An actor with a request that throws an exception.
class ExceptionActor extends ActorBase {

    //Create an ExceptionActor.
    ExceptionActor(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Returns an exception request.
    AsyncRequest<Void> exceptionAReq() {
        return new AsyncRequest<Void>(getReactor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                throw new IllegalStateException(); //Throw an exception when the request is processed.
            }
        };
    }
}

//An actor with an exception handler.
class ExceptionHandlerActor extends ActorBase {

    //An actor with a request that throws an exception.
    private final ExceptionActor exceptionActor;

    //Create an exception handler actor with a reference to an exception actor.
    ExceptionHandlerActor(final ExceptionActor _exceptionActor, final Reactor _reactor) throws Exception {
        exceptionActor = _exceptionActor;
        initialize(_reactor);
    }

    //Returns a test request.
    AsyncRequest<String> testAReq() {
        return new AsyncRequest<String>(getReactor()) {
            AsyncRequest<String> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {

                //Create and assign an exception handler.
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception _exception) throws Exception {
                        if (_exception instanceof IllegalStateException) {
                            //Returns a result if an IllegalStateException was thrown.
                            return "got IllegalStateException, as expected";
                        } else //Otherwise rethrow the exception.
                            throw _exception;
                    }
                });

                //Create an exception request and send it to the exception actor for processing.
                //The thrown exception is then caught by the assigned exception handler.
                exceptionActor.exceptionAReq().send(getMessageProcessor(), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) throws Exception {
                        dis.processAsyncResponse("can not get here");
                    }
                });
            }
        };
    }

}