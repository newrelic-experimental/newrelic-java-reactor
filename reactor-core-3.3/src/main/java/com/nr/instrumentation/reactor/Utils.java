package com.nr.instrumentation.reactor;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

public class Utils {

	private static final String NRHOOKKEY = Utils.class.getName();
	public static boolean initialized = false;
	
	private final static List<String> ofInterest;
	
	static {
		ofInterest = new ArrayList<String>();
		ofInterest.add("publishOn");
		ofInterest.add("subscribeOn");
		ofInterest.add("subscribeOnCallable");
		ofInterest.add("subscribeOnValue");
	}

	public static void init() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Hooks.resetOnEachOperator(NRHOOKKEY);
			}
		});
		setHook();
		initialized = true;
	}

	private static void setHook() {
		Hooks.onEachOperator(NRHOOKKEY, Operators.lift((scannable,coreSubscriber) -> {
			String name = scannable.name();
			if(ofInterest.contains(name)) {
				if(!(coreSubscriber instanceof NRContextLifter)) {
					NewRelic.getAgent().getLogger().log(Level.FINE, "Wrapped coresubscriber {0} for scannable {1}", coreSubscriber, name);
					return new NRContextLifter<>(coreSubscriber,name);
				}
			}
			return coreSubscriber;
		}
		));
	}


}