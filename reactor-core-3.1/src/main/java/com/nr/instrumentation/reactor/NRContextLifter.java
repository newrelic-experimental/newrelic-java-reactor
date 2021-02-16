package com.nr.instrumentation.reactor;


import org.reactivestreams.Subscription;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;

import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

public class NRContextLifter<T> implements CoreSubscriber<T> {

	private CoreSubscriber<T> coreSubscriber;
	private static boolean isTransformed = false;
	public Token token = null;
	private String tracedName;

	public NRContextLifter(CoreSubscriber<T> c, String rType) {
		coreSubscriber= c;
		if(!isTransformed) {
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			isTransformed = true;
		}
	}

	@Override
	@Trace(async=true/*,excludeFromTransactionTrace=true*/)
	public void onNext(T t) {
		if(token != null) {
			token.link();
		}
		coreSubscriber.onNext(t);
	}

	@Override
	@Trace(async=true/*,excludeFromTransactionTrace=true*/)
	public void onError(Throwable t) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		coreSubscriber.onError(t);
	}

	@Override
	@Trace(async=true/*,excludeFromTransactionTrace=true*/)
	public void onComplete() {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		coreSubscriber.onComplete();
	}

	@Override
	public void onSubscribe(Subscription s) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		tracedName = traced.getMetricName();
		if(!tracedName.equals("NoOpTracedMethod") && token == null) {
			token = NewRelic.getAgent().getTransaction().getToken();
			if(token != null && !token.isActive()) {
				token.expire();
				token = null;
			}
		}
		coreSubscriber.onSubscribe(s);
	}

	@Override
	public Context currentContext() {
		return coreSubscriber.currentContext();
	}

}
