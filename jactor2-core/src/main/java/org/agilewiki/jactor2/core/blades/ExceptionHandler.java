package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

/**
 * Exception handlers are used to alter how exceptions are processed.
 * <p>
 * By default, an exception which occurs while processing a call or doSend request is
 * returned as a result to the source processing or caller.
 * And for 1-way messages, the default is to simply log the exception as a warning.
 * Exception processing is specific to the request/event message being processed.
 * An application can set the exception handler for the request/event currently being processed using the
 * Reactor.setExceptionHandler method.
 * </p>
 * <p>
 * When a targetReactor receives an exception as a result, the exception is handled the same way as any other
 * exception, by either passing it to an exception handler or returning it to the source of the request
 * being processed. On the other hand when a caller receives an exception as a result, the exception is
 * simply rethrown rather than passing it to the application logic as a response.
 * </p>
 * <p>
 * An exception handler can be selective as to which exceptions will be handled.
 * Any exceptions that it does not handle are simply rethrown, with default exception handling then
 * processing the exception.
 * Exception handlers can also return a result, providing they have access to the appropriate
 * AsyncResponseProcessor object.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.threading.BasicPlant;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 *
 * public class ExceptionHandlerSample {
 *
 *     public static void main(final String[] _args) throws Exception {
 *
 *         //A facility with two threads.
 *         final BasicPlant plant = new BasicPlant(2);
 *
 *         try {
 *
 *             //Create an ExceptionBlade.
 *             ExceptionBlade exceptionBlade = new ExceptionBlade(new NonBlockingReactor(plant));
 *
 *             try {
 *                 //Create and call an exception request.
 *                 exceptionBlade.exceptionAReq().call();
 *                 System.out.println("can not get here");
 *             } catch (IllegalStateException ise) {
 *                 System.out.println("got first IllegalStateException, as expected");
 *             }
 *
 *             //Create an ExceptionHandlerBlade.
 *             ExceptionHandlerBlade exceptionHandlerBlade =
 *                     new ExceptionHandlerBlade(exceptionBlade, new NonBlockingReactor(plant));
 *             //Create a test request, call it and print the results.
 *             System.out.println(exceptionHandlerBlade.testAReq().call());
 *
 *         } finally {
 *             //shutdown the facility
 *             plant.close();
 *         }
 *     }
 * }
 *
 * //A blades with a request that throws an exception.
 * class ExceptionBlade extends BladeBase {
 *
 *     //Create an ExceptionBlade.
 *     ExceptionBlade(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns an exception request.
 *     AsyncRequest&lt;Void&gt; exceptionAReq() {
 *         return new AsyncBladeRequest&lt;Void&gt;() {
 *
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *                 throw new IllegalStateException(); //Throw an exception when the request is processed.
 *             }
 *         };
 *     }
 * }
 *
 * //A blades with an exception handler.
 * class ExceptionHandlerBlade extends BladeBase {
 *
 *     //A blades with a request that throws an exception.
 *     private final ExceptionBlade exceptionBlade;
 *
 *     //Create an exception handler blades with a reference to an exception blades.
 *     ExceptionHandlerBlade(final ExceptionBlade _exceptionBlade, final Reactor _messageProcessor) throws Exception {
 *         exceptionBlade = _exceptionBlade;
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns a test request.
 *     AsyncRequest&lt;String&gt; testAReq() {
 *         return new AsyncBladeRequest&lt;String&gt;() {
 *             AsyncRequest&lt;String&gt; dis = this;
 *
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *
 *                 //Create and assign an exception handler.
 *                 setExceptionHandler(new ExceptionHandler&lt;String&gt;() {
 *                     {@literal @}Override
 *                     public String processException(final Exception _exception) throws Exception {
 *                         if (_exception instanceof IllegalStateException) {
 *                             //Returns a result if an IllegalStateException was thrown.
 *                             return "got IllegalStateException, as expected";
 *                         } else //Otherwise rethrow the exception.
 *                             throw _exception;
 *                     }
 *                 });
 *
 *                 //Create an exception request and send it to the exception blades for processing.
 *                 //The thrown exception is then caught by the assigned exception handler.
 *                 send(exceptionBlade.exceptionAReq(), dis, "can not get here");
 *             }
 *         };
 *     }
 *
 * }
 *
 * Output:
 * got first IllegalStateException, as expected
 * got IllegalStateException, as expected
 * </pre>
 */
abstract public class ExceptionHandler<RESPONSE_TYPE> {
    /**
     * Process an exception or rethrow it.
     *
     * @param e The exception to be processed.
     */
    abstract public RESPONSE_TYPE processException(final Exception e)
            throws Exception;

    public void processException(final Exception e, final AsyncResponseProcessor _asyncResponseProcessor)
            throws Exception {
        _asyncResponseProcessor.processAsyncResponse(processException(e));
    }
}
