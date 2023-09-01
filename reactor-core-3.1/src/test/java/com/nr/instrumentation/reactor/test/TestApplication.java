package com.nr.instrumentation.reactor.test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;
import com.newrelic.agent.introspec.Introspector;
import com.newrelic.agent.introspec.TracedMetricData;
import com.newrelic.api.agent.Trace;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = "reactor.core")
public class TestApplication {
	
	private static final String txn1 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoSub";
	private static final String txn2 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.NRRunnableWrapper/run";
	private static final String txn3 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoPub";
	

	@Test
	public void doMonoSubscribeOnTest() {
		/**
		 * This should result in two transaction, one is the main thread and the other is the result of the subscribe action
		 */
		testMonoSub();

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		Assert.assertEquals("Expected two transactions", finishedTransactionCount, 2L);
		
		Collection<String> txnNames = introspector.getTransactionNames();
		boolean contains = txnNames.contains(txn1) & txnNames.contains(txn2);
		Assert.assertTrue(contains);
		
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn2);
		Set<String> names = metrics.keySet();
		Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
		Assert.assertTrue(names.contains("Custom/com.nr.instrumentation.reactor.test.TestApplication/doSubscribeAction"));
	}
	
	@Test
	public void doMonoPublishOnTest() {
		/**
		 * This should result in two transaction, one is the main thread and the other is the result of the publish action
		 */
		testMonoPub();

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		Assert.assertEquals("Expected two transaction", finishedTransactionCount, 2L);
		
		Collection<String> txnNames = introspector.getTransactionNames();
		boolean contains = txnNames.contains(txn3) & txnNames.contains(txn2);
		Assert.assertTrue(contains);
		
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn2);
		Set<String> names = metrics.keySet();
		for(String name : names) {
			System.out.println("trace: "+ name);
		}
		Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
		//Assert.assertTrue(names.contains("Custom/com.nr.instrumentation.reactor.test.TestApplication/doSubscribeAction"));
	}
	
	@Trace(dispatcher = true)
	public void testMonoPub() {
		System.out.println("Enter testMonoPub");
		Mono<String> mono = getStringMono().publishOn(Schedulers.single());

		mono.subscribeWith(new MonoCoreSubscriber());
		String result = mono.block();
		
		System.out.println("Exit testMonoPub with result: "+ result);
		
	}

	@Trace(dispatcher = true)
	public void testMonoSub() {
		System.out.println("Enter testMonoSub");
		ResultConsumer c = new ResultConsumer();
		Mono<String> mono = getStringMono().subscribeOn(Schedulers.single()).doOnSuccess(c);
		
		mono.subscribe(s -> doSubscribeAction(s));
		
		
		System.out.println("Exit testMonoSub wit result: " + c.result);

	}

	public Mono<String> getStringMono() {
		
		
		return Mono.fromCallable(() -> {
			try {
				Thread.sleep(100L);
			} catch(Exception e) {
				
			}
			return "hello";
		});
	}
	
	@Trace
	public void doSubscribeAction(String s) {
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Result is "+s);
	}
	
	private class ResultConsumer implements Consumer<String> {
		
		String result;

		@Override
		@Trace
		public void accept(String t) {
			result = t;
		}
		
	}
}
