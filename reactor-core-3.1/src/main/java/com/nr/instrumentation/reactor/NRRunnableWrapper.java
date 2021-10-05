package com.nr.instrumentation.reactor;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;

public class NRRunnableWrapper implements Runnable {
	
	private Runnable delegate = null;
	
	//private Token token = null;
	private NRReactorHeaders headers;
	private static boolean isTransformed = false;
	
	public NRRunnableWrapper(Runnable r, NRReactorHeaders h) {
		delegate = r;
		headers = h;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(dispatcher=true)
	public void run() {
		boolean ignore = true;
		if(headers != null) {
			if(!headers.isEmpty()) {
				NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
				ignore = false;
			}
		}
		if(ignore) {
			NewRelic.getAgent().getTransaction().ignore();
		}
		if(delegate != null) {
			delegate.run();
		}
	}

}
