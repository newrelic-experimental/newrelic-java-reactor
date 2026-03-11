package com.nr.labs.test.reactor;

import java.util.function.Consumer;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class ErrorConsumer implements Consumer<Throwable> {

    @Override
    @Trace(dispatcher = true)
    public void accept(Throwable t) {
        NewRelic.noticeError(t);
        System.out.println("Error was thrown: "+t.getMessage());
    }

}
