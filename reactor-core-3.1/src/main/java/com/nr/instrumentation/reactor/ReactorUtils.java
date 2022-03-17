package com.nr.instrumentation.reactor;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Transaction;

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
		//Hooks.onEachOperator("NewRelicWrapper",asOperator());		
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
	
	public static boolean activeTransaction() {
		Boolean active = transactionActive.get();
		Transaction txn = NewRelic.getAgent().getTransaction();
		// if transaction is NoOp then there is no active transaction
		boolean isNoOp = txn.getClass().getName().toLowerCase().contains("noop");
		if(!active && !isNoOp) {
			setActive();
			active = true;
		} else if(active && isNoOp) {
			deActivate();
			active = false;
		}
		return active;
	}
	
	public static void setActive() {
		transactionActive.set(true);
	}
	
	public static void deActivate() {
		transactionActive.set(false);
	}
}
