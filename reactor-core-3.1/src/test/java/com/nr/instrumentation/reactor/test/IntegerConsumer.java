package com.nr.instrumentation.reactor.test;

import java.util.function.Consumer;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class IntegerConsumer implements Consumer<Integer> {
	
	private String source = null;
	
	public IntegerConsumer(String s) {
		source = s;
	}

	@Override
	@Trace(dispatcher = true)
	public void accept(Integer t) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Test","Reactor","StringConsumer",source);
		System.out.println("IntegerConsumer for " + source + " received: " + t);
	}

}
