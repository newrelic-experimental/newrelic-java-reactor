package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.Set;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class MonoReturnMethodMatcher implements MethodMatcher {
	
	@Override
	public Method[] getExactMethods() {
		return null;
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		Type type = Type.getReturnType(desc);
		String classname = type.getClassName();
		return isMono(classname);
	}

	private boolean isMono(String className) {
		if(className.equals("reactor.core.publisher.Mono") || className.equals("reactor.core.publisher.MonoProcessor")) {
			return true;
		}
				
		return false;
	}

}
