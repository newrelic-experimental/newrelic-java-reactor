package reactor.core.dispatch;

import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.BaseClass)
public abstract class AbstractLifecycleDispatcher {

}
