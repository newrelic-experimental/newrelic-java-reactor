package reactor.core.publisher;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.nr.instrumentation.reactor.Utils;

@Weave(type=MatchType.BaseClass)
public class Flux<T> {

	
	@WeaveAllConstructors
	public Flux() {
		if(!Utils.initialized) {
			Utils.init();
		}
	}
}
