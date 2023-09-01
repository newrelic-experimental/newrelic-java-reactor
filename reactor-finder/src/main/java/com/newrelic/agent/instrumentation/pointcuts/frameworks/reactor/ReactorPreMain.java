package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.TracerService;
import com.newrelic.agent.core.CoreService;
import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.NewRelic;

public class ReactorPreMain {
	
	protected static final String TRACER_FACTORY_NAME = "ReactorReturning";

	public static void premain(String s, Instrumentation inst) {
		
		initialize();
	}
	
	private static void initialize() {
		boolean b = setup();
		if(!b) {
			Executors.newSingleThreadExecutor().submit(new SetupProcess());
		}
	}
	
	private static boolean setup() {
		TracerService tracerService = ServiceFactory.getTracerService();
		ClassTransformerService classTransformerService = ServiceFactory.getClassTransformerService();
		CoreService coreService = ServiceFactory.getCoreService();
		if(classTransformerService != null && coreService != null && tracerService != null) {
			
			tracerService.registerTracerFactory(TRACER_FACTORY_NAME, new ReactorFactory());
			
			InstrumentationContextManager contextMgr = classTransformerService.getContextManager();
			InstrumentationProxy proxy = coreService.getInstrumentation();
			if(contextMgr != null && proxy != null) {
				ReactorClassTransformer transformer = new ReactorClassTransformer(contextMgr);
				ClassAndMethodMatcher matcher = new ReactorMatcher();
				transformer.addMatcher(matcher);
				NewRelic.getAgent().getLogger().log(Level.FINE, "Reactor Finder transformer started");
				return true;
			}
		}
		return false;
	}
	
	private static class SetupProcess implements Runnable {
		
		

		@Override
		public void run() {
			boolean setupComplete = false;
			
			while(!setupComplete) {
				setupComplete = setup();
				if(!setupComplete) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
					}
				}
			}
			
		}
		
	}
}
