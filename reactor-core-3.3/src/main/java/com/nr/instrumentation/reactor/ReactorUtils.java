package com.nr.instrumentation.reactor;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.Transaction;
import com.newrelic.agent.transaction.TransactionTimer;

import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

public class ReactorUtils {
	
	private static ThreadLocal<Boolean> transactionActive = new ThreadLocal<Boolean>() {

		@Override
        protected Boolean initialValue() {
            return false;
        }	
	
	};
	
	public static boolean initialized = false;
	
	public static final String NRHEADERS = "NEWRELIC_HEADERS";

	
	public static void initialize() {
		initialized = true;
		Hooks.onLastOperator("NewRelicWrapper",asOperator());
		ReactorDispatcher.get();
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> Function<? super Publisher<T>, ? extends Publisher<T>> asOperator() {
		
		return Operators.lift(new BiFunction<Scannable, CoreSubscriber<? super T>, CoreSubscriber<? super T>>() {

			@Override
			public CoreSubscriber<? super T> apply(Scannable t, CoreSubscriber<? super T> u) {
					return new NRSubscriberWrapper(u,t);
			}
		});
		
	}
	
	public static boolean timerStarted() {
		com.newrelic.agent.Transaction txn = com.newrelic.agent.Transaction.getTransaction(false);
		if(txn != null) {
			TransactionTimer timer = txn.getTransactionTimer();
			if(timer != null) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean activeTransaction() {
		if(transactionActive.get()) return true;
		com.newrelic.api.agent.TracedMethod tracer = AgentBridge.getAgent().getTransaction().getTracedMethod();
		if(tracer != null) {
			transactionActive.set(true);
			return true;
		}
		return false;
	}
	
	public static void setActive() {
		transactionActive.set(true);
	}
	
	public static void deActivate() {
		transactionActive.set(false);
	}
}
