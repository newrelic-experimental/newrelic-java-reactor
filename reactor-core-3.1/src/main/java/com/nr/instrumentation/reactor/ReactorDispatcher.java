package com.nr.instrumentation.reactor;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransportType;

import reactor.core.CoreSubscriber;

public class ReactorDispatcher {
	
	private static volatile ReactorDispatcher instance = null;
	
	public static ReactorDispatcher get() {
		if(instance == null) {
			instance = new ReactorDispatcher();
		}
		return instance;
	}
	
	private ReactorDispatcher() {
		AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
	}
	
	
	@Trace(dispatcher = true)
	public static <T> void startOnNextTransaction(String name,CoreSubscriber<T> sub, T t, NRReactorHeaders headers) {
		ReactorUtils.setActive();
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor",name,"onNext");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null && ReactorUtils.activeTransaction()) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			}
		}
		sub.onNext(t);
	}

	@Trace(dispatcher = true)
	public static <T> void startOnCompleteTransaction(String name,CoreSubscriber<T> sub, NRReactorHeaders headers) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor",name,"onComplete");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null && ReactorUtils.activeTransaction()) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			}
		}
		sub.onComplete();
	}
	
	@Trace(dispatcher = true)
	public static <T> void startOnErrorTransaction(String name,CoreSubscriber<T> sub, NRReactorHeaders headers, Throwable t) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor",name,"onError");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null && ReactorUtils.activeTransaction()) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			}
		}
		sub.onError(t);
	}

}
