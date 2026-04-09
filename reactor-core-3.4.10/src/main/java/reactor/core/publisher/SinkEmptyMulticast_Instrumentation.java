package reactor.core.publisher;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "reactor.core.publisher.SinkEmptyMulticast")
class SinkEmptyMulticast_Instrumentation<T> {

    @Trace
    public Sinks.EmitResult tryEmitEmpty() {
        return Weaver.callOriginal();
    }

    @Trace
    public Sinks.EmitResult tryEmitError(Throwable t) {
        return Weaver.callOriginal();
    }
}
