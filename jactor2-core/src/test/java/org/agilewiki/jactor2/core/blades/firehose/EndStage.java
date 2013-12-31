package org.agilewiki.jactor2.core.blades.firehose;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class EndStage extends IsolationBladeBase implements DataProcessor {

    public EndStage(final BasicPlant _plant) throws Exception {
        initialize(new IsolationReactor());
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                processAsyncResponse(null);
            }
        };
    }
}
