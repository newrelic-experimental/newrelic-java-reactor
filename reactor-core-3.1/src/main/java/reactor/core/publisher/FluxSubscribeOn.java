package reactor.core.publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.reactor.NRContextLifter;

import reactor.core.CoreSubscriber;

@Weave
abstract class FluxSubscribeOn<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void subscribe(CoreSubscriber<? super T> actual) {
		if(!(actual instanceof NRContextLifter)) {
			NRContextLifter<? super T> wrapper = new NRContextLifter(actual, "FluxSubscribeOnCallable");
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
		}
		Weaver.callOriginal();
	}
}
