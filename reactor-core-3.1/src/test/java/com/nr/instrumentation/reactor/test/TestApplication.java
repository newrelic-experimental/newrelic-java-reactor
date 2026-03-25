package com.nr.instrumentation.reactor.test;

import java.util.Collection;
import java.util.List;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = "reactor.core")
public class TestApplication {
	
	private static final String txn1 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoSub";
	private static final String txn2 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoPub";
	private static final String txn3 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testFluxSub";
	private static final String txn4 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testFluxPub";
	private static final String[] fluxArray = {"Message 1", "Message 2", "Message 3", "Message 4"};

//	@Test
//	public void doMonoSubscribeOnTest() {
//		testMonoSub();
//
//		Introspector introspector = InstrumentationTestRunner.getIntrospector();
//		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
//		Assert.assertEquals("Expected one transactions", 1, finishedTransactionCount);
//
//		Collection<String> txnNames = introspector.getTransactionNames();
//		boolean contains = txnNames.contains(txn1); // & txnNames.contains(txn2);
//		Assert.assertTrue(contains);
//
//		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn1);
//		Set<String> names = metrics.keySet();
//		Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
//		Assert.assertTrue(names.contains("Custom/com.nr.instrumentation.reactor.test.TestApplication/doSubscribeAction"));
//	}
	
	@Test
	public void doMonoPublishOnTest() {
		testMonoPub();

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
//		Assert.assertEquals("Expected one transaction", 1, finishedTransactionCount);

		Collection<String> txnNames = introspector.getTransactionNames();
		System.out.println("txnNames: " + txnNames);
//		boolean contains = txnNames.contains(txn2); // & txnNames.contains(txn2);
//		Assert.assertTrue(contains);

//		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn2);
//		Set<String> names = metrics.keySet();
//		for(String name : names) {
//			System.out.println("trace: "+ name);
//		}
		//Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
	}

//	@Test
//	public void doFluxPublishOnTest() {
//		testFluxPub();
//
//		Introspector introspector = InstrumentationTestRunner.getIntrospector();
//		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
//		Assert.assertEquals("Expected one transaction", 1, finishedTransactionCount);
//
//		Collection<String> txnNames = introspector.getTransactionNames();
//		boolean contains = txnNames.contains(txn4); // & txnNames.contains(txn2);
//		Assert.assertTrue(contains);
//
//		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn4);
//		Set<String> names = metrics.keySet();
//		for(String name : names) {
//			System.out.println("trace: "+ name);
//		}
//		Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
//	}
//
//	@Test
//	public void doFluxSubscribeOnTest() {
//		testFluxSub();
//		Introspector introspector = InstrumentationTestRunner.getIntrospector();
//		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
//		Assert.assertEquals("Expected one transaction", 1, finishedTransactionCount);
//
//		Collection<String> txnNames = introspector.getTransactionNames();
//		boolean contains = txnNames.contains(txn3); // & txnNames.contains(txn2);
//		Assert.assertTrue(contains);
//
//		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn3);
//		Set<String> names = metrics.keySet();
//		for(String name : names) {
//			System.out.println("trace: "+ name);
//		}
//		Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
//	}
//
//	@Test
//	public void doScheduleTest() {
//		testScheduler();
//		Introspector introspector = InstrumentationTestRunner.getIntrospector();
//		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
//		Assert.assertEquals("Expected one transaction", 1, finishedTransactionCount);
//		String txnName = introspector.getTransactionNames().iterator().next();
//		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txnName);
//		Set<String> names = metrics.keySet();
//		System.out.println("traces for "+ txnName);
//		for(String name : names) {
//			System.out.println("trace: "+ name);
//		}
//	}


	@Trace(dispatcher = true)
	public void testScheduler() {
		System.out.println("Enter testScheduler");
		Mono<String> mono = getStringMono();
		Scheduler scheduler = Schedulers.single();
		scheduler.schedule(() -> {
			mono.subscribeWith(new MonoCoreSubscriber());
			String result = mono.block();

			System.out.println("Exit testScheduler with result: "+ result);

		});


	}

	@Trace(dispatcher = true)
	public void testMonoPub() {
		System.out.println("Enter testMonoPub");
		Await await = new Await();
		Mono<String> mono = getStringMono();

		mono.publishOn(Schedulers.single()).subscribe(new TestCoreSubscriber(await));
		await.await();

		System.out.println("Exit testMonoPub with result: " + await.getResult());

	}

	@Trace(dispatcher = true)
	public void testMonoSub() {
		System.out.println("Enter testMonoSub");
		ResultConsumer c = new ResultConsumer();
		Mono<String> mono = getStringMono().subscribeOn(Schedulers.single()).doOnSuccess(c);

		mono.subscribe(s -> doSubscribeAction(s));

		String result = mono.block();
		System.out.println("Exit testMonoSub wit result: " + c.result);

	}

	@Trace(dispatcher = true)
	public void testFluxSub() {
		System.out.println("Enter testFluxSub");

		Flux<String> flux = getStringFlux().subscribeOn(Schedulers.single());

		flux.subscribe(s -> doSubscribeAction(s));

		List<String> list = flux.collectList().block();
		System.out.println("Exit testFluxSub with result: " + list);
	}

	@Trace(dispatcher = true)
	public void testFluxPub() {
		System.out.println("Enter testMonoPub");
		Flux<String> flux = getStringFlux().publishOn(Schedulers.single());

		flux.subscribeWith(new MonoCoreSubscriber());

		List<String> list = flux.collectList().block();

		System.out.println("Exit testMonoPub with result: "+ list);

	}



	public Flux<String> getStringFlux() {
		return Flux.fromArray(fluxArray);
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
