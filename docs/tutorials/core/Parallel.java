import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Parallel extends NonBlockingBladeBase {
    private final long count;
    
    public Parallel(final NonBlockingReactor _reactor, final long _count)
            throws Exception {
        initialize(_reactor);
        count = _count;
    }
    
    public AsyncRequest<Void> runAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            
            final AsyncResponseProcessor<Void> sleepResponseProcessor = 
                new AsyncResponseProcessor<Void>() {
                
                long i = 0;
                
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    i++;
                    if (i == count)
                        dis.processAsyncResponse(null);
                }
            };
            
            public void processAsyncRequest() throws Exception {
                NonBlockingReactor reactor = getReactor();
                Plant plant = reactor.getPlant();
                long j = 0;
                while(j < count) {
                    j++;
                    Delay delay = new Delay(new BlockingReactor(plant));
                    send(delay.sleepSReq(100), sleepResponseProcessor);
                }
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        final long count = 10L;
        Plant plant = new Plant(10);
        try {
            Parallel parallel = new Parallel(new NonBlockingReactor(plant), count);
            AsyncRequest<Void> runAReq = parallel.runAReq();
            final long before = System.currentTimeMillis();
            runAReq.call();
            final long after = System.currentTimeMillis();
            final long duration = after - before;
            Printer printer = Printer.stdoutAReq(plant).call();
            printer.printlnSReq("Parallel Test with 10 Threads").call();
            printer.printlnSReq("count: " + count).call();
            printer.printlnSReq("sleep duration: 100 milliseconds").call();
            printer.printlnSReq("total time: " + duration + " milliseconds").call();
        } finally {
            plant.close();
        }
    }
}
