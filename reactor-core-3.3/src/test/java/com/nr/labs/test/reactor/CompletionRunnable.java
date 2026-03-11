package com.nr.labs.test.reactor;

import com.newrelic.api.agent.Trace;

public class CompletionRunnable implements Runnable {

    @Override
    @Trace(dispatcher = true)
    public void run() {
        System.out.println("Emitter has completed");
    }
}
