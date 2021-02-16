package reactor.core.publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.reactor.NRContextLifter;

import reactor.core.CoreSubscriber;

@Weave
abstract class FluxPublishOn<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CoreSubscriber<? super T> subscribeOrReturn(CoreSubscriber<? super T> actual) {
		NRContextLifter<? super T> wrapper = new NRContextLifter(actual, "FluxPublishOn");
		Token token = NewRelic.getAgent().getTransaction().getToken();
		if(token != null) {
			if(token.isActive()) {
				wrapper.token = token;
			} else {
				token.expire();
				token = null;
			}
		}
		actual = wrapper;
		return Weaver.callOriginal();
	}
}
