package com.newrelic.agent.instrumentation.pointcuts.frameworks.reactor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.instrumentation.PointCutClassTransformer;
import com.newrelic.agent.service.AbstractService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.NewRelic;

public class ReactorLoaderService extends AbstractService {

	private ExecutorService executor = null;

	public ReactorLoaderService() {
		super("ReactorLoaderService");
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected void doStart() throws Exception {
		
		ClassTransformerService classTransformerService = ServiceFactory.getClassTransformerService();
		if(classTransformerService != null) {
			PointCutClassTransformer classTransformer = classTransformerService.getClassTransformer();
			if(classTransformer != null) {
				ReactorPointcut reactorPointcut = new ReactorPointcut(classTransformer);
				boolean b = classTransformerService.addTraceMatcher(reactorPointcut, "Reactor");
				NewRelic.getAgent().getLogger().log(Level.FINE, "Result of adding ReactorPointcut is {0}", b);
			} else {
				NewRelic.getAgent().getLogger().log(Level.FINE, "Could not load matcher because ClassTransformerService has not started");
				startExecutor();
			}
		} else {
			NewRelic.getAgent().getLogger().log(Level.FINE, "Could not load matcher because ClassTransformerService is null");
			startExecutor();
		}
	}
	
	private void startExecutor() {
		executor = Executors.newSingleThreadExecutor();
		RunCheck runCheck = new RunCheck();
		executor.submit(runCheck);
		NewRelic.getAgent().getLogger().log(Level.FINE, "Submit RunCheck to executor");		
	}

	private boolean addTraceMatcher(ClassTransformerService classTransformerService) {
		PointCutClassTransformer classTransformer = classTransformerService.getClassTransformer();
		ReactorPointcut reactorPointcut = new ReactorPointcut(classTransformer);
		return classTransformerService.addTraceMatcher(reactorPointcut, "Reactor");
	}

	private void shutdownExecutor() {
		executor.shutdown();
		NewRelic.getAgent().getLogger().log(Level.FINE, "ReactorLoaderService executor has shut down");
	}
	
	@Override
	protected void doStop() throws Exception {

	}

	private class RunCheck implements Runnable {

		@Override
		public void run() {
			boolean done = false;
			while(!done) {
				ClassTransformerService classTransformerService = ServiceFactory.getClassTransformerService();
				if(classTransformerService != null) {
					PointCutClassTransformer classTransformer = classTransformerService.getClassTransformer();
					if(classTransformer != null) {
						done = true;
						boolean b = addTraceMatcher(classTransformerService);
						NewRelic.getAgent().getLogger().log(Level.FINE, "Result of adding ReactorPointcut is {0}", b);
					}
				} else {
					try {
						Thread.sleep(5000L);
					} catch (InterruptedException e) {
					}
				}
			}
			shutdownExecutor();
		}

	}

}
