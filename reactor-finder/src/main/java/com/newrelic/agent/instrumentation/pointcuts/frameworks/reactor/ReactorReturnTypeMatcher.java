package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.Set;

import com.newrelic.agent.instrumentation.methodmatchers.ManyMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class ReactorReturnTypeMatcher extends ManyMethodMatcher {
	
	private static MethodMatcher monoReturnMatcher = new MonoReturnMethodMatcher();
	private static MethodMatcher fluxReturnMatcher = new FluxReturnMethodMatcher();
	private static MethodMatcher publisherReturnMatcher = new PublisherReturnMethodMatcher();


	public ReactorReturnTypeMatcher() {
		super(monoReturnMatcher,fluxReturnMatcher, publisherReturnMatcher);
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		if(monoReturnMatcher.matches(access, name, desc, annotations)) {
			return true;
		}
		if(fluxReturnMatcher.matches(access, name, desc, annotations)) {
			return true;
		}
		if(publisherReturnMatcher.matches(access, name, desc, annotations)) {
			return true;
		}
		return false;
	}

}
