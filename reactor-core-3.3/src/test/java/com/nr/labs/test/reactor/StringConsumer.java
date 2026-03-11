package com.nr.labs.test.reactor;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class StringConsumer implements Consumer<String> {

    private String name = "Unknown";

    public StringConsumer(String s) {
        if(s != null && !s.isEmpty()) {
            name = s;
        }
    }

    @Override
    @Trace(dispatcher = true)
    public void accept(String t) {
        Logger logger = NewRelic.getAgent().getLogger();
        logger.log(Level.FINE,new Exception("StringConsumer call"), "Call to StringConsumer.accept");
        Transaction transaction = AgentBridge.getAgent().getTransaction();
        Map<String, Object> attributes = transaction.getAgentAttributes();
        for(String key : attributes.keySet()) {
            logger.log(Level.FINE, "Attribute key: {0} has value {1}", key, attributes.get(key));
        }
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom",name,"consume");
        System.out.println("Consumer for "+name+" received string "+ t);
    }

}
