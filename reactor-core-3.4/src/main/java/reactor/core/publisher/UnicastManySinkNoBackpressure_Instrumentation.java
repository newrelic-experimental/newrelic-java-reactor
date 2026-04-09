package reactor.core.publisher;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "reactor.core.publisher.UnicastManySinkNoBackpressure")
class UnicastManySinkNoBackpressure_Instrumentation<T> {

    @Trace
    public Sinks.EmitResult tryEmitComplete() {
        return Weaver.callOriginal();
    }

    @Trace
    public Sinks.EmitResult tryEmitError(Throwable t) {
        return Weaver.callOriginal();
    }

    @Trace
    public Sinks.EmitResult tryEmitNext(T t) {
        return Weaver.callOriginal();
    }
}
