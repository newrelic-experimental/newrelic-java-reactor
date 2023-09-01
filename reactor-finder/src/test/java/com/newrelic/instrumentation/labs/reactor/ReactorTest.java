package com.newrelic.instrumentation.labs.reactor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.reactor.test.MonoReturning;
import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;
import com.newrelic.agent.introspec.Introspector;

import reactor.core.publisher.Mono;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = {"reactor.core"})
public class ReactorTest {

	@Test
	public void monoReturnTest() {
		MonoReturning returningMono = new MonoReturning();
		String name = "Doug";
		Mono<String> mono = returningMono.findAccountName(name);
		String acct = mono.block();
		System.out.println("Found account: "+ acct + " for Customer: "+name);
		
		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		//assertTrue(finishedTransactionCount == 1);
		System.out.println("Finished transactions: "+ finishedTransactionCount);

	}
	
}
