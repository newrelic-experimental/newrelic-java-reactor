package reactor.core.publisher;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.reactor.ReactorUtils;

@Weave(originalName="reactor.core.publisher.Mono",type = MatchType.BaseClass)
public abstract class Mono_instrumentation {
	
	protected static <T> Mono<T> onAssembly(Mono<T> source) {
		if(!ReactorUtils.initialized) {
			ReactorUtils.initialize();
		}
		return Weaver.callOriginal();
	}
}
