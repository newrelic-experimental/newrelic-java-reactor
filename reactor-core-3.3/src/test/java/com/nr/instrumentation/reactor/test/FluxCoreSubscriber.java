package com.nr.instrumentation.reactor.test;

import org.reactivestreams.Subscription;

import com.newrelic.api.agent.Trace;

import reactor.core.CoreSubscriber;

public class FluxCoreSubscriber implements CoreSubscriber<Integer> {

	@Override
	@Trace
	public void onNext(Integer t) {
		System.out.println("Received Integer for onNext: " + t);
	}

	@Override
	@Trace
	public void onError(Throwable t) {
		System.out.println("Received error for onError: " + t);
		t.printStackTrace();
	}

	@Override
	@Trace
	public void onComplete() {
		System.out.println("Flux has completed");
	}

	@Override
	@Trace
	public void onSubscribe(Subscription var1) {
		System.out.println("Flux was subscribed to by : " + var1);
		var1.request(10);
	}

}
