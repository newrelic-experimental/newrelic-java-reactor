package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher.Match;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;

public class ReactorClassTransformer implements ContextClassTransformer {

	private final InstrumentationContextManager contextManager;
	private final Map<String, ClassMatchVisitorFactory> matchers = new HashMap<String, ClassMatchVisitorFactory>();

	public ReactorClassTransformer(InstrumentationContextManager mgr) {
		contextManager = mgr;
	}

	protected void addMatcher(ClassAndMethodMatcher matcher) {
		OptimizedClassMatcherBuilder builder = OptimizedClassMatcherBuilder.newBuilder();
		builder.addClassMethodMatcher(matcher);
		ClassMatchVisitorFactory matchVisitor = builder.build();
		matchers.put(matcher.getClass().getSimpleName(), matchVisitor);
		contextManager.addContextClassTransformer(matchVisitor, this);
	}

	protected void removeMatcher(ClassAndMethodMatcher matcher) {
		ClassMatchVisitorFactory matchVisitor = matchers.get(matcher.getClass().getSimpleName());
		if(matchVisitor != null) {
			contextManager.removeMatchVisitor(matchVisitor);
		}
	}


	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer, InstrumentationContext context, Match match)
					throws IllegalClassFormatException {
		for (Method method : match.getMethods()) {
			for (ClassAndMethodMatcher matcher : match.getClassMatches().keySet()) {
				if (matcher.getMethodMatcher().matches(MethodMatcher.UNSPECIFIED_ACCESS, method.getName(),
						method.getDescriptor(), match.getMethodAnnotations(method))) {
					context.putTraceAnnotation(method, TraceDetailsBuilder.newBuilder().setTracerFactoryName(ReactorPreMain.TRACER_FACTORY_NAME)
							.setInstrumentationSourceName("New Relic Labs").setInstrumentationType(InstrumentationType.TraceAnnotation).setDispatcher(false).build());
				}

			}
		}
		return null;
	}

}
