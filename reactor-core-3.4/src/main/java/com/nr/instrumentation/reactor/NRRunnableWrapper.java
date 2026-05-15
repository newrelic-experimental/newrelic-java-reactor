package com.nr.instrumentation.reactor;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;

import java.util.logging.Level;

public class NRRunnableWrapper implements Runnable {

	@NewField
	private static int activeTokens = 0;

	private NRRunnableWrapper() {
		super();
	}
	private Runnable delegate = null;
	
	private Token token = null;
	private static boolean isTransformed = false;
	
	public NRRunnableWrapper(Runnable r, Token t) {
		NewRelic.recordMetric("ReactorTokens/NRRunnableWrapper/Created", 1.0f);
		activeTokens++;
		NewRelic.recordMetric("ReactorErrors/NRRunnableWrapper/Active", activeTokens);
		delegate = r;
		token = t;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(async=true)
	public void run() {
		if(token != null) {
			token.linkAndExpire();
			NewRelic.recordMetric("ReactorTokens/NRRunnableWrapper/Expired", 1.0f);
			activeTokens--;
			NewRelic.recordMetric("ReactorErrors/NRRunnableWrapper/Active", activeTokens);
			token = null;
		}
		if(delegate != null) {
			delegate.run();
		}
	}

}
