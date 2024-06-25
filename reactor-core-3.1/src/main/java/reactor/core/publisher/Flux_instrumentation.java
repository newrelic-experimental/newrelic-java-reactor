package reactor.core.publisher;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.reactor.ReactorUtils;

@Weave(originalName="reactor.core.publisher.Flux",type = MatchType.BaseClass)
public abstract class Flux_instrumentation {
	
	protected static <T> Flux<T> onAssembly(Flux<T> source) {
		if(!ReactorUtils.initialized) {
			ReactorUtils.initialize();
		}
		return Weaver.callOriginal();
	}

}
