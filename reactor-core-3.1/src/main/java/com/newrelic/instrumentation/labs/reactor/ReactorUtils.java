package com.newrelic.instrumentation.labs.reactor;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.Transaction;

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
	
	@SuppressWarnings("deprecation")
	public static boolean activeTransaction() {
		if(transactionActive.get()) return true;
		Transaction transaction = AgentBridge.getAgent().getTransaction();
		return transaction != null ? transaction.isStarted() : false;
	}
	
	public static void setActive() {
		transactionActive.set(true);
	}
	
	public static void deActivate() {
		transactionActive.set(false);
	}
}
