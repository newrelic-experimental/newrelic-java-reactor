package com.nr.labs.test.reactor;

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
    @Trace(dispatcher = true)
    public void onNext(String t) {
        Logger logger = NewRelic.getAgent().getLogger();
        Transaction transaction = AgentBridge.getAgent().getTransaction();
        Map<String, Object> attributes = transaction.getAgentAttributes();
        logger.log(Level.FINE,new Exception("TestCoreSubscriber call"), "Call to TestCoreSubscriber.onNext");
        for(String key : attributes.keySet()) {
            logger.log(Level.FINE, "Attribute key: {0} has value {1}", key, attributes.get(key));
        }
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
        Logger logger = NewRelic.getAgent().getLogger();
        Transaction transaction = AgentBridge.getAgent().getTransaction();
        Map<String, Object> attributes = transaction.getAgentAttributes();
        logger.log(Level.FINE,new Exception("TestCoreSubscriber call"), "Call to TestCoreSubscriber.onComplete");
        for(String key : attributes.keySet()) {
            logger.log(Level.FINE, "Attribute key: {0} has value {1}", key, attributes.get(key));
        }
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
        s.request(1);
    }

}
