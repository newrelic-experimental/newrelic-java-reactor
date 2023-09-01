package reactor.core.scheduler;

import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.reactor.NRReactorHeaders;
import com.nr.instrumentation.reactor.NRRunnableWrapper;

import reactor.core.Disposable;

@Weave(type=MatchType.Interface)
public abstract class Scheduler {
	
	@Trace
	public Disposable schedule(Runnable task) {
		if(!(task instanceof NRRunnableWrapper)) {
			NRReactorHeaders nrHeaders = new NRReactorHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(nrHeaders);

			if(!nrHeaders.isEmpty()) {
				NRRunnableWrapper wrapper = new NRRunnableWrapper(task, nrHeaders);
				task = wrapper;
			}
		}
		return Weaver.callOriginal();
	}
	
	@Weave(type=MatchType.Interface)
	public static class Worker {
		
		public Disposable schedule(Runnable task) {
			if(!(task instanceof NRRunnableWrapper)) {
				NRReactorHeaders nrHeaders = new NRReactorHeaders();
				NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(nrHeaders);

				if(!nrHeaders.isEmpty()) {
					NRRunnableWrapper wrapper = new NRRunnableWrapper(task, nrHeaders);
					task = wrapper;
				}
			}
			return Weaver.callOriginal();
		}
		
		public Disposable schedule(Runnable task, long delay, TimeUnit unit) {
			if(!(task instanceof NRRunnableWrapper)) {
				NRReactorHeaders nrHeaders = new NRReactorHeaders();
				NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(nrHeaders);

				if(!nrHeaders.isEmpty()) {
					NRRunnableWrapper wrapper = new NRRunnableWrapper(task, nrHeaders);
					task = wrapper;
				}
			}
			return Weaver.callOriginal();
		}
	}

}
