package reactor.core.publisher;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.nr.instrumentation.reactor.Utils;

@Weave(type=MatchType.BaseClass,originalName="reactor.core.publisher.Mono")
public class Mono_instrumentation<T> {

	@WeaveAllConstructors
	public Mono_instrumentation() {
		if(!Utils.initialized) {
			Utils.init();
		}
	}
	
	
}
