package reactor.core.publisher;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.reactor.NRContextLifter;

import reactor.core.CoreSubscriber;

@Weave
abstract class FluxSubscribeOnCallable<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void subscribe(CoreSubscriber<? super T> actual) {
		NRContextLifter<? super T> wrapper = new NRContextLifter(actual, "FluxSubscribeOnCallable");
		actual = wrapper;
		Weaver.callOriginal();
	}

}
