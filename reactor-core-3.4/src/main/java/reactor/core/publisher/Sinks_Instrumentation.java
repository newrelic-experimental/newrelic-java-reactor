package reactor.core.publisher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.WeaveAllConstructors;
import com.newrelic.api.agent.weaver.Weaver;
import reactor.util.annotation.Nullable;

/**
 * Provides support for the use of Sinks
 */
@Weave(originalName = "reactor.core.publisher.Sinks")
public class Sinks_Instrumentation {

    @Weave(originalName = "reactor.core.publisher.Sinks$Many", type = MatchType.Interface)
    public static class Many_Instrumentation<T> {
        @NewField
        Token tokenForMany = null;

        @WeaveAllConstructors
        public Many_Instrumentation() {
            if(tokenForMany == null) {
                Token t = NewRelic.getAgent().getTransaction().getToken();
                if(t != null) {
                    if(t.isActive()) {
                        tokenForMany = t;
                    }  else {
                        t.expire();
                        t = null;
                    }
                }
            }
        }

        @Trace(async=true)
        public void emitComplete(Sinks.EmitFailureHandler failureHandler) {
            if(tokenForMany != null) {
                tokenForMany.linkAndExpire();
                tokenForMany = null;
            }
        }

        @Trace(async=true)
        public void emitError(Throwable error, Sinks.EmitFailureHandler failureHandler) {
            if(tokenForMany != null) {
                tokenForMany.linkAndExpire();
                tokenForMany = null;
            }
            Weaver.callOriginal();
        }

        @Trace(async=true)
        public void emitNext(T t, Sinks.EmitFailureHandler failureHandler) {
            if(tokenForMany != null) {
                tokenForMany.link();
            }
            Weaver.callOriginal();
        }

        @Trace(async=true)
        public Sinks.EmitResult tryEmitNext(T value) {
            if(tokenForMany != null) {
                tokenForMany.link();
            }
            return Weaver.callOriginal();
        }

        @Trace(async=true)
        public Sinks.EmitResult tryEmitComplete() {
            if(tokenForMany != null) {
                tokenForMany.linkAndExpire();
                tokenForMany = null;
            }
            return Weaver.callOriginal();
        }

        @Trace(async=true)
        public Sinks.EmitResult tryEmitError(Throwable error) {
            if(tokenForMany != null) {
                tokenForMany.linkAndExpire();
                tokenForMany = null;
            }
            return Weaver.callOriginal();
        }
    }
}
