package reactor.core.publisher;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.nr.instrumentation.reactor.ReactorUtils;

@Weave(originalName="reactor.core.publisher.Flux",type = MatchType.BaseClass)
public abstract class Flux_instrumentation {
	
	@WeaveAllConstructors
	public Flux_instrumentation() {
		if(!ReactorUtils.initialized) {
			ReactorUtils.initialize();
		}
	}

}
