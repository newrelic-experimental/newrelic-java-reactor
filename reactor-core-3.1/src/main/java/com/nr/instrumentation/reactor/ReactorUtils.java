package com.nr.instrumentation.reactor;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

import org.reactivestreams.Publisher;

import com.newrelic.api.agent.NewRelic;

import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

public class ReactorUtils {
	
	
	public static boolean initialized = false;
	
	public static final String NRHEADERS = "NEWRELIC_HEADERS";

	
	public static void initialize() {
		initialized = true;
		//Hooks.onEachOperator("NewRelicWrapper",asOperator());		
		Hooks.onLastOperator("NewRelicWrapper",asOperator());
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
}
