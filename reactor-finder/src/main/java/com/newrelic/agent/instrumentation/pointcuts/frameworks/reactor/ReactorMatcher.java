package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class ReactorMatcher implements ClassAndMethodMatcher {
	
	private ClassMatcher classMatcher = new ReactorClassMatcher();
	private MethodMatcher methodMatcher = new ReactorReturnTypeMatcher();

	@Override
	public ClassMatcher getClassMatcher() {
		return classMatcher;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return methodMatcher;
	}

}
