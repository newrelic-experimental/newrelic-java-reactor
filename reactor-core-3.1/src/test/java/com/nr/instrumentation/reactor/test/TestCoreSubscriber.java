package com.nr.instrumentation.reactor.test;

import java.util.Map;
import java.util.logging.Level;

import org.reactivestreams.Subscription;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

import reactor.core.CoreSubscriber;

public class TestCoreSubscriber implements CoreSubscriber<String> {

    private Await await = null;
    String result = null;

    public TestCoreSubscriber(Await a) {
        await = a;
    }

    @Override
    @Trace
    public void onNext(String t) {
        System.out.println("call to onNext with string: " + t);
        result = t;
    }

    @Override
    @Trace
    public void onError(Throwable t) {
        System.out.println("Object has error: "+t.getMessage());
        if(await != null) {
            await.setResult(t.getMessage());
        }

    }

    @Override
    @Trace
    public void onComplete() {
        if(await != null) {
            synchronized(await) {
                await.setResult(result);
            }
        }
        System.out.println("Object has completed");
    }

    @Override
    @Trace
    public void onSubscribe(Subscription s) {
        s.request(1);
    }

}
