package com.nr.instrumentation.reactor;

import com.newrelic.api.agent.NewRelic;

public class ReactorConfig {

    public static final boolean errorsEnabled = NewRelic.getAgent().getConfig()
            .getValue("reactor.errors.enabled", false);

    private ReactorConfig() {
    }

}
