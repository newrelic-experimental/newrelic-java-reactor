package com.nr.instrumentation.reactor;

import org.reactivestreams.Subscription;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransportType;

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
		if (transaction != null) {
			transaction.insertDistributedTraceHeaders(headers);
		}
		
	}

	@Override
	@Trace(dispatcher=true)
	public void onNext(T t) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor","CoreSubscriber",name,"onNext");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			} else {
				transaction.ignore();
			}
		}

		actual.onNext(t);
	}

	@Override
	@Trace(dispatcher=true)
	public void onError(Throwable t) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor","CoreSubscriber",name,"onError");
		NewRelic.noticeError(t);
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			} else {
				transaction.ignore();
			}
		}
		actual.onError(t);
	}

	@Override
	@Trace(dispatcher=true)
	public void onComplete() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor","CoreSubscriber",name,"onComplete");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			} else {
				transaction.ignore();
			}
		}
		actual.onComplete();
	}

	@Override
	public void onSubscribe(Subscription s) {
		subscription = s;
		actual.onSubscribe(this);
	}

	@Override
	@Trace(dispatcher=true)
	public void request(long n) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor","Subscriber",name,"request");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			} else {
				transaction.ignore();
			}
		}
		subscription.request(n);
	}

	@Override
	@Trace(dispatcher=true)
	public void cancel() {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Reactor","Subscriber",name,"cancel");
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (transaction != null) {
			if (headers != null && !headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, headers);
			} else {
				transaction.ignore();
			}
		}
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
