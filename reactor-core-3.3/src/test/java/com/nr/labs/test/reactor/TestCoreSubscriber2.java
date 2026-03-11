package com.nr.labs.test.reactor;

import org.reactivestreams.Subscription;

import com.newrelic.api.agent.Trace;

import reactor.core.CoreSubscriber;

public class TestCoreSubscriber2 implements CoreSubscriber<String> {

    private Await await = null;
    String result = null;
    private int numberOfItems = 1;

    public TestCoreSubscriber2(Await a, int n) {
        await = a;
        numberOfItems = n;
    }

    @Override
    @Trace(dispatcher = true)
    public void onNext(String t) {
        System.out.println("call to onNext with string: " + t);
        result = t;
    }

    @Override
    @Trace(dispatcher = true)
    public void onError(Throwable t) {
        System.out.println("Object has error: "+t.getMessage());
        if(await != null) {
            await.setResult(t.getMessage());
        }

    }

    @Override
    @Trace(dispatcher = true)
    public void onComplete() {
        if(await != null) {
            synchronized(await) {
                await.setResult(result);
            }
        }
        System.out.println("Object has completed");
    }

    @Override
    @Trace(dispatcher = true)
    public void onSubscribe(Subscription s) {
        s.request(numberOfItems);
    }

}
