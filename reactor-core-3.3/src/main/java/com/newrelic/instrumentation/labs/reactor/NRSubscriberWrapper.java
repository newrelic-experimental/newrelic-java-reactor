package com.newrelic.instrumentation.labs.reactor;

import org.reactivestreams.Subscription;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;

import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.Fuseable.QueueSubscription;
import reactor.core.Scannable;

public class NRSubscriberWrapper<T> implements CoreSubscriber<T>, QueueSubscription<T> {

	private CoreSubscriber<T> actual = null;

	private static boolean isTransformed = false;

	private Subscription subscription = null;

	private NRReactorHeaders headers = null;

	private String name = null;

	public NRSubscriberWrapper(CoreSubscriber<T> sub, Scannable s) {
		if(!isTransformed) {
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
			isTransformed = true;
		}
		actual = sub;	
		name = s.name();
		if(name == null || name.isEmpty()) name = "Scannable";
		headers = new NRReactorHeaders();
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null && ReactorUtils.activeTransaction()) {
			try {
				transaction.insertDistributedTraceHeaders(headers);
			} catch(Exception e) {
				String exceptionName = e.getClass().getSimpleName();
				NewRelic.incrementCounter("NRLabs/Reactor/NRSubscriberWrapper/Exception/"+exceptionName);
			}
		}
	}

	@Override
	public void onNext(T t) {
		if(!ReactorUtils.activeTransaction()) {
			ReactorDispatcher.startOnNextTransaction(name, actual, t, headers);
		} else {
			actual.onNext(t);
		}
	}

	@Override
	public void onError(Throwable t) {
		if(!ReactorUtils.activeTransaction()) {
			ReactorDispatcher.startOnErrorTransaction(name, actual, headers,t);
		} else {
			ReactorUtils.deActivate();
			actual.onError(t);
		}
	}

	@Override
	public void onComplete() {
		if(!ReactorUtils.activeTransaction()) {
			ReactorDispatcher.startOnCompleteTransaction(name, actual, headers);
		} else {
			ReactorUtils.deActivate();
			actual.onComplete();
		}
	}

	@Override
	public void onSubscribe(Subscription s) {
		subscription = s;
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		boolean isNoOp = traced.getClass().getSimpleName().toLowerCase().contains("noop");
		if(!ReactorUtils.activeTransaction() || isNoOp) {
			ReactorDispatcher.startOnSubscribeTransaction(name, actual, s, headers);
		} else {
			if(headers == null) {
				headers = new NRReactorHeaders();
			}
			if(headers.isEmpty()) {
				Transaction transaction = NewRelic.getAgent().getTransaction();
				if (transaction != null && ReactorUtils.activeTransaction()) {
					try {
						transaction.insertDistributedTraceHeaders(headers);
					} catch(Exception e) {
						String exceptionName = e.getClass().getSimpleName();
						NewRelic.incrementCounter("NRLabs/Reactor/NRSubscriberWrapper/Exception/"+exceptionName);
					}
				}
			}
			actual.onSubscribe(this);
		}
	}

	@Override
	@Trace
	public void request(long n) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor",name,"request");
		subscription.request(n);
	}

	@Override
	@Trace
	public void cancel() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor",name,"cancel");
		subscription.cancel();
	}

	@Override
	public T poll() {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public void clear() {

	}

	@Override
	public int requestFusion(int requestedMode) {
		return Fuseable.NONE;
	}

}
