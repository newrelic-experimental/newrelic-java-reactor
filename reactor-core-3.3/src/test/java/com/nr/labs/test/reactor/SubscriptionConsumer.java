package com.nr.labs.test.reactor;

import java.util.function.Consumer;

import org.reactivestreams.Subscription;

import com.newrelic.api.agent.Trace;

public class SubscriptionConsumer implements Consumer<Subscription> {

    @Override
    @Trace(dispatcher = true)
    public void accept(Subscription t) {
        System.out.println("Received subscription " + t);
    }

}
