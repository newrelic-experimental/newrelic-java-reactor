package reactor.core.scheduler;

import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.reactor.NRRunnableWrapper;

import reactor.core.Disposable;

@Weave(type=MatchType.Interface)
public abstract class Scheduler {
	
	@Trace
	public Disposable schedule(Runnable task) {
		if(!(task instanceof NRRunnableWrapper)) {
			Token t = NewRelic.getAgent().getTransaction().getToken();
			if(t != null && t.isActive()) {
				NRRunnableWrapper wrapper = new NRRunnableWrapper(task, t);
				task = wrapper;
			} else if(t != null) {
				t.expire();
				t = null;
			}
		}
		return Weaver.callOriginal();
	}
	
	@Weave(type=MatchType.Interface)
	public static class Worker {
		
		public Disposable schedule(Runnable task) {
			if(!(task instanceof NRRunnableWrapper)) {
				Token t = NewRelic.getAgent().getTransaction().getToken();
				if(t != null && t.isActive()) {
					NRRunnableWrapper wrapper = new NRRunnableWrapper(task, t);
					task = wrapper;
				} else if(t != null) {
					t.expire();
					t = null;
				}
			}
			return Weaver.callOriginal();
		}
		
		public Disposable schedule(Runnable task, long delay, TimeUnit unit) {
			if(!(task instanceof NRRunnableWrapper)) {
				Token t = NewRelic.getAgent().getTransaction().getToken();
				if(t != null && t.isActive()) {
					NRRunnableWrapper wrapper = new NRRunnableWrapper(task, t);
					task = wrapper;
				} else if(t != null) {
					t.expire();
					t = null;
				}
			}
			return Weaver.callOriginal();
		}
	}

}
