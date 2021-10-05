package reactor.core.publisher;

import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.reactor.ReactorUtils;

@Weave(originalName="reactor.core.publisher.Mono",type = MatchType.BaseClass)
public abstract class Mono_instrumentation {
	
	protected static <T> Mono<T> onAssembly(Mono<T> source) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to Mono.onAssemby");
		if(!ReactorUtils.initialized) {
			ReactorUtils.initialize();
		}
		return Weaver.callOriginal();
	}
}
