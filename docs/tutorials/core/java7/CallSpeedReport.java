import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.SOp;

public class CallSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger();
            SOp<Long> pingSOp = ponger.pingSOp();
            final long before = System.nanoTime();
            long i = 0L;
            while (i < count) {
                i += 1;
                long j = pingSOp.call();
            }
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Call Timings", duration, count);
        } finally {
            plant.close();
        }
    }
}