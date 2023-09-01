package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.logging.Level;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.AbstractTracerFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.TracerFlags;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.api.agent.NewRelic;

public class ReactorFactory extends AbstractTracerFactory {

	@Override
	public Tracer doGetTracer(Transaction transaction, ClassMethodSignature sig, Object object, Object[] args) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to doGetTracer for sig: {0},{1} and object: {2})", sig.getClassName(),sig.getMethodName(),object);
		String classname = sig.getClassName();
		String methodName = sig.getMethodName();
		
		int flags = DefaultTracer.DEFAULT_TRACER_FLAGS | TracerFlags.DISPATCHER;
		
		MetricNameFormat metricName = new SimpleMetricNameFormat("Custom/Reactor/ReactorMethod/"+classname+"/"+methodName);
		DefaultTracer tracer = new DefaultTracer(transaction, sig, object, metricName, flags);
		return tracer;
	}

}
