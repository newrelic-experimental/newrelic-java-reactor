package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.Set;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.util.asm.Utils;

public class FluxReturnMethodMatcher implements MethodMatcher {

	@Override
	public Method[] getExactMethods() {
		return null;
	}

	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		Type type = Type.getReturnType(desc);
		String classname = type.getClassName();
		return isFlux(classname);
	}

	private boolean isFlux(String className) {
		if(Utils.isPrimitiveType(className) || className.endsWith("[]") || className.startsWith("com.newrelic")) {
			return false;
		}
		if(className.equals("reactor.core.publisher.Flux")) {
			return true;
		}
		if(className.startsWith("reactor.core.publisher") && className.endsWith("Processor")) {
			return true;
		}
		
		return false;
	}
}
