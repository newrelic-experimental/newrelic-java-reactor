package com.nr.instrumentation.reactor.test;

import java.util.function.Consumer;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class StringConsumer implements Consumer<String> {
	
	private String source = null;
	
	public StringConsumer(String s) {
		source = s;
	}

	@Override
	@Trace(dispatcher = true)
	public void accept(String t) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Test","Reactor","StringConsumer",source);
		System.out.println("StringConsumer for " + source + " received: " + t);
	}

}
