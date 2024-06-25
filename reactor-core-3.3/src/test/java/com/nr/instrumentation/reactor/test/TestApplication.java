package com.nr.instrumentation.reactor.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;
import com.newrelic.agent.introspec.Introspector;
import com.newrelic.agent.introspec.TraceSegment;
import com.newrelic.agent.introspec.TracedMetricData;
import com.newrelic.agent.introspec.TransactionEvent;
import com.newrelic.agent.introspec.TransactionTrace;
import com.newrelic.api.agent.Trace;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = "reactor.core")
public class TestApplication {
	
	private static final String txn1 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoSub";
	private static final String txn2 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.NRRunnableWrapper/run";
	private static final String txn3 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoPub";
	private static final String txn4 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMono";
	private static final String txn5 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testFlux";	
	private static final String txn6 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testFluxPub";	
	private static final String txn7 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplication/testMonoPubSub";

	@Test
	public void doFluxTest() {
		/**
		 * This should result in one transaction, one is the main thread.  All actions are performed on the main thread
		 */
		System.out.println("=================================================");
		System.out.println("Call to doFluxTest");
		testFlux();
		System.out.println("=================================================");
		
		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		Assert.assertEquals("Expected one transaction", finishedTransactionCount, 1L);
		System.out.println("In doFluxTest, got " + finishedTransactionCount +" transactions");
		Collection<String> txnNames = introspector.getTransactionNames();
		
		Assert.assertTrue(txnNames.contains(txn5));
		
		for(String name : txnNames) {
			Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction(name);
			System.out.println("Found " + traces.size() + " transaction traces for " + name);
			for(TransactionTrace txnTrace : traces) {
				printTrace(txnTrace);
			}
		}
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn5);
		System.out.println("Transaction " + txn5 + " has " + metrics.size() + " metrics");
		for(String key : metrics.keySet()) {
			TracedMetricData traceMetricData = metrics.get(key);
			System.out.println("TraceMetricData: name - " + traceMetricData.getName() + ", call count - " + traceMetricData.getCallCount() + ", total time - " + traceMetricData.getTotalTimeInSec());
		}
		
	}
	
	@Test
	public void doFluxPubOnTest() {
		/**
		 * This should result in 12 transactions, one is the main thread, and 11 that happen asynchronously on Flux Subscriber actions. 
		 */
		System.out.println("=================================================");
		System.out.println("Call to doFluxPubOnTest");
		testFluxPub();
		System.out.println("=================================================");
		
		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		Assert.assertEquals("Expected one transaction", finishedTransactionCount, 12L);
		System.out.println("In doFluxPubOnTest, got " + finishedTransactionCount +" transactions");
		Collection<String> txnNames = introspector.getTransactionNames();
		
		Assert.assertTrue(txnNames.contains(txn6));
		
		Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction("OtherTransaction/Custom/com.nr.instrumentation.reactor.NRRunnableWrapper/run");
		Assert.assertTrue(traces.size() == 11);
				
	}
	
	@Test
	public void doFluxSubOnTest() {
		/**
		 * This should result in 2 transactions, one is the main thread, and one on the subscriber thread. All actions except onSubscribe are on the subscriber thread
		 */
		System.out.println("=================================================");
		System.out.println("Call to doFluxSubOnTest");
		testFluxSub();
		System.out.println("=================================================");
		
		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		Assert.assertEquals("Expected one transaction", finishedTransactionCount, 2L);
		System.out.println("In doFluxPubOnTest, got " + finishedTransactionCount +" transactions");
		Collection<String> txnNames = introspector.getTransactionNames();
		
		for(String name : txnNames) {
			System.out.println("Transaction name: " + name);
			Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction(name);
			System.out.println("Found " + traces.size() + " transaction traces for " + name);
			for(TransactionTrace txnTrace : traces) {
				printTrace(txnTrace);
			}
		}
//		Assert.assertTrue(txnNames.contains(txn6));
		
//		Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction("OtherTransaction/Custom/com.nr.instrumentation.reactor.NRRunnableWrapper/run");
//		Assert.assertTrue(traces.size() == 11);
				
	}
	
	@Test
	public void doMonoSubscribeOnTest() {
		/**
		 * This should result in two transaction, one is the main thread and the other is the result of the subscribe action
		 */
		System.out.println("=================================================");
		System.out.println("Call to doMonoSubscribeOnTest");
		testMonoSub();
		System.out.println("=================================================");

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		System.out.println("In doMonoSubscribeOnTest, got " + finishedTransactionCount +" transactions");
		Assert.assertEquals("Expected two transactions", finishedTransactionCount, 2L);
		
		Collection<String> txnNames = introspector.getTransactionNames();
		boolean contains = txnNames.contains(txn1) & txnNames.contains(txn2);
		Assert.assertTrue(contains);
		
		for(String name : txnNames) {
			Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction(name);
			System.out.println("Found " + traces.size() + " transaction traces for " + name);
			for(TransactionTrace txnTrace : traces) {
				printTrace(txnTrace);
			}
		}
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn2);
		Set<String> names = metrics.keySet();
		Assert.assertTrue(names.contains("Custom/Reactor/ScheduledRunnable/SubscribeOnSubscriber"));
		Assert.assertTrue(names.contains("Custom/com.nr.instrumentation.reactor.test.TestApplication/doSubscribeAction"));
	}
	
	@Test
	public void doMonoPublishOnTest() {
		/**
		 * This should result in three transactions, one is the main thread and the two is the result of the publish action
		 */
		System.out.println("=================================================");
		System.out.println("Call to doMonoPublishOnTest");

		testMonoPub();
		System.out.println("=================================================");

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		System.out.println("In doMononPublishOnTest, got " + finishedTransactionCount +" transactions");
		Collection<String> txnNames = introspector.getTransactionNames();
		Assert.assertEquals("Expected three transactions", finishedTransactionCount, 3L);
		
		boolean contains = txnNames.contains(txn3) & txnNames.contains(txn2);
		Assert.assertTrue(contains);

		for(String name : txnNames) {
			Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction(name);
			System.out.println("Found " + traces.size() + " transaction traces for " + name);
			for(TransactionTrace txnTrace : traces) {
				printTrace(txnTrace);
			}
		}
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn2);
		Set<String> names = metrics.keySet();
		for(String name : names) {
			System.out.println("trace: "+ name);
		}
		Assert.assertTrue(names.contains("Custom/Reactor/ScheduledRunnable/PublishOnSubscriber"));
		//Assert.assertTrue(names.contains("Custom/com.nr.instrumentation.reactor.test.TestApplication/doSubscribeAction"));
	}
	
	@Test
	public void doMonoPublishAndSubTest() {
		/**
		 * This should result in five transactions, one is the main thread and the two are the result of the publish action and two are result of the subscribe action
		 */
		System.out.println("=================================================");
		System.out.println("Call to doMonoPublishAndSubTest");

		testMonoPubSub();
		System.out.println("=================================================");

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		System.out.println("In doMonoPublishAndSubTest, got " + finishedTransactionCount +" transactions");
		Collection<String> txnNames = introspector.getTransactionNames();
		Assert.assertEquals("Expected five transactions", finishedTransactionCount, 5L);
		
		Assert.assertTrue(txnNames.contains(txn7));
	}
	
	@Test
	public void doMonoNoOns() {
		/**
		 * This should result in one transaction, one is the main thread.  All actions are performed on the main thread
		 */
		System.out.println("=================================================");
		System.out.println("Call to testMono");
		testMono();
		System.out.println("=================================================");
		
		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		System.out.println("In doMonoNoOnsTest, got " + finishedTransactionCount +" transactions");
		Collection<String> txnNames = introspector.getTransactionNames();
		int i = 1;
		for(String txName : txnNames) {
			Collection<TransactionEvent> txnEvents = introspector.getTransactionEvents(txName);
			System.out.println("Transaction " + i + ": " + txName + " has " + txnEvents.size() + " transaction events");
			i++;
		}
		Assert.assertEquals("Expected one transaction", finishedTransactionCount,1L);
		for(String name : txnNames) {
			Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction(name);
			System.out.println("Found " + traces.size() + " transaction traces for " + name);
			for(TransactionTrace txnTrace : traces) {
				printTrace(txnTrace);
			}
		}
		
		Assert.assertTrue(txnNames.contains(txn4));
		
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn4);
		System.out.println("Transaction: " + txn4 + " has " + metrics.size() + " metrics");
		Set<String> names = metrics.keySet();
		for(String name : names) {
			System.out.println("trace: "+ name);
		}
		//Assert.assertTrue(names.contains("Java/com.nr.instrumentation.reactor.NRRunnableWrapper/run"));
	}
	
	private Flux<Integer> getIntegerFlux() {
		Integer[] numbers = new Integer[] {5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
		
		Stream<Integer> integerStream = Stream.of(numbers);
		Flux<Integer> flux = Flux.fromStream(integerStream);
		return flux.doOnNext(new IntegerConsumer("OnNext")).doOnError(t -> {
			System.out.println("Flux error");
			t.printStackTrace();
		});
	}
	
	@Trace (dispatcher = true) 
	public void testFlux() {
		CompletableFuture<Boolean> done = new CompletableFuture<Boolean>();

		Flux<Integer> flux = getIntegerFlux();
		Flux<Integer> actual = flux.map(s -> {
			pause(50L);
			return s;
		}).doOnComplete(() -> {
			System.out.println("recieved all numbers");
			done.complete(true);
		}
		).doOnError(t -> {
			System.out.println("Error: " + t);
			t.printStackTrace();
			done.complete(false);
		});
		actual.subscribe(new FluxCoreSubscriber());
		try {
			Boolean b = done.get();
			System.out.println("Result of operation is " + b);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Trace (dispatcher = true) 
	public void testFluxPub() {
		CompletableFuture<Boolean> done = new CompletableFuture<Boolean>();

		Flux<Integer> flux = getIntegerFlux();
		Flux<Integer> actual = flux.map(s -> {
			pause(50L);
			return s;
		}).doOnComplete(() -> {
			System.out.println("recieved all numbers");
			done.complete(true);
		}
		).doOnError(t -> {
			System.out.println("Error: " + t);
			t.printStackTrace();
			done.complete(false);
		}).publishOn(Schedulers.single(), 10);
		actual.subscribe(new FluxCoreSubscriber());
		try {
			Boolean b = done.get();
			System.out.println("Result of operation is " + b);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Trace (dispatcher = true) 
	public void testFluxPubSub() {
		CompletableFuture<Boolean> done = new CompletableFuture<Boolean>();

		Flux<Integer> flux = getIntegerFlux();
		Flux<Integer> actual = flux.map(s -> {
			pause(50L);
			return s;
		}).doOnComplete(() -> {
			System.out.println("recieved all numbers");
			done.complete(true);
		}
		).doOnError(t -> {
			System.out.println("Error: " + t);
			t.printStackTrace();
			done.complete(false);
		}).publishOn(Schedulers.single(), 10);
		actual.subscribe(new FluxCoreSubscriber());
		try {
			Boolean b = done.get();
			System.out.println("Result of operation is " + b);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Trace (dispatcher = true) 
	public void testFluxSub() {
		CompletableFuture<Boolean> done = new CompletableFuture<Boolean>();

		Flux<Integer> flux = getIntegerFlux();
		Flux<Integer> actual = flux.map(s -> {
			pause(50L);
			return s;
		}).doOnComplete(() -> {
			System.out.println("recieved all numbers");
			done.complete(true);
		}
		).doOnError(t -> {
			System.out.println("Error: " + t);
			t.printStackTrace();
			done.complete(false);
		}).subscribeOn(Schedulers.single());
		actual.subscribe(new FluxCoreSubscriber());
		try {
			Boolean b = done.get();
			System.out.println("Result of operation is " + b);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	


	@Trace(dispatcher = true) 
	public void testMono() {
		Mono<String> mono = getStringMono();
		mono.subscribeWith(new MonoCoreSubscriber());
		String result = mono.block();
		System.out.println("Exit testMono with result: "+ result);
		
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
	public void testMonoPubSub() {
		System.out.println("Enter testMonoPub");
		Mono<String> mono = getStringMono().publishOn(Schedulers.elastic()).subscribeOn(Schedulers.elastic());

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
	
	public Flux<String> getStringFlux(int numberOfElements) {
		
		
		return null;
	}

	public Mono<String> getStringMono() {
		
		
		Mono<String> mono = Mono.fromCallable(() -> {
			try {
				Thread.sleep(100L);
			} catch(Exception e) {
				
			}
			return "hello";
		});
		
		return mono.doOnNext(new StringConsumer("OnNext")).doOnSuccess(new StringConsumer("OnSuccess")).doOnSubscribe(sub -> {
			System.out.println("Call to onSubscribe for subcriber: " + sub);
		});
	}
	
	@Trace
	public void doSubscribeAction(String s) {
		pause(100L);
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
	
	private static void printTrace(TransactionTrace txnTrace) {
		System.out.println("Transaction Trace ");
		TraceSegment initialSeg = txnTrace.getInitialTraceSegment();
		
		printSegment(initialSeg, 1);
		printChildren(initialSeg, 2);
		
		System.out.println("\t took " + txnTrace.getResponseTimeInSec() + " seconds");
		
				
	}
	
	private static void printChildren(TraceSegment segment, int indent) {
		List<TraceSegment> children = segment.getChildren();
		
		if(children != null && !children.isEmpty()) {
			for(TraceSegment child : children) {
				printSegment(child, indent+1);
				printChildren(child,indent+2);
			}
		}
	}
	
	private static void printSegment(TraceSegment segment, int indent) {
		for(int i = 0; i<indent;i++) System.out.print('\t');
		System.out.println(segment.getName() + ", " + segment.getMethodName() + ", "	+ segment.getClassName( )+ "Called " + segment.getCallCount() + " times");
	}
	
	private static void pause(long ms) {
		if(ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
