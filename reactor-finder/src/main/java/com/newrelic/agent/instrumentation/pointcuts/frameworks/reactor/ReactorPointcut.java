package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import com.newrelic.agent.MetricNames;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.PointCutClassTransformer;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;


public class ReactorPointcut extends TracerFactoryPointCut {

	public ReactorPointcut(PointCutClassTransformer classTransformer) {
		super(new PointCutConfiguration("reactor"), new ReactorClassMatcher(), new ReactorReturnTypeMatcher());
	}

	@Override
	protected Tracer doGetTracer(Transaction transaction, ClassMethodSignature sig, Object reactor, Object[] args) {
		
		return new ReactorMethodTracer(transaction, sig, reactor);
	}

	@Override
	public boolean isDispatcher() {
		return true;
	}
	
	
	private static class ReactorMethodTracer extends OtherRootTracer {
		
		public ReactorMethodTracer(Transaction transaction, ClassMethodSignature sig, Object reactor) {
			super(transaction, sig, reactor, new ClassMethodMetricNameFormat(sig, reactor, MetricNames.OTHER_TRANSACTION+"/Reactor"));
		}
	}
	
}
