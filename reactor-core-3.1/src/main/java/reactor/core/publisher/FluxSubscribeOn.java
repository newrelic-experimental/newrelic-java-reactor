package reactor.core.publisher;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import reactor.core.CoreSubscriber;

/**
 * Included simply so the verifier only works up to version 3.3.0.RELEASE
 * 
 * @author dhilpipre
 *
 * @param <T>
 */

@Weave
abstract class FluxSubscribeOn<T> {

	
	public void subscribe(CoreSubscriber<? super T> actual) {
		Weaver.callOriginal();
	}
}
