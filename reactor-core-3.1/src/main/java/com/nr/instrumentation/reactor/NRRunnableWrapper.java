package com.nr.instrumentation.reactor;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRRunnableWrapper implements Runnable {
	
	private Runnable delegate = null;
	
	//private Token token = null; //gulab
	private NRReactorHeaders headers = null;
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
		if(headers != null) {
			HeaderUtils.acceptHeaders(headers);
		}
		if(delegate != null) {
			delegate.run();
		}
	}

}
