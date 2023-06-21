package com.nr.instrumentation.reactor.test;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;
import com.newrelic.agent.introspec.Introspector;
import com.newrelic.agent.introspec.SpanEvent;
import com.newrelic.agent.introspec.TraceSegment;
import com.newrelic.agent.introspec.TracedMetricData;
import com.newrelic.agent.introspec.TransactionTrace;
import com.newrelic.api.agent.Trace;

import reactor.core.publisher.Flux;

import reactor.core.scheduler.Schedulers;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = "reactor.core")
public class TestApplicationFlux {
	
	private static final String txn1 = "OtherTransaction/Custom/com.nr.instrumentation.reactor.test.TestApplicationFlux/testFluxSub";
	
	@Test
	public void doFluxSubscribeOnTest() {
		testFluxSub();

		Introspector introspector = InstrumentationTestRunner.getIntrospector();
		int finishedTransactionCount = introspector.getFinishedTransactionCount(5000);
		System.out.println("Finished transaction count: "+finishedTransactionCount);
		assertEquals("Finished transaction count should be :", 1,finishedTransactionCount);
		
		Collection<String> txnNames = introspector.getTransactionNames();
		String txName = null;
		for(String tName : txnNames) {
			System.out.println("Transaction Name: "+tName);
			txName=tName;
		}
		assertEquals("Transaction Name :", txn1,txName);
		
		Map<String, TracedMetricData> metrics = introspector.getMetricsForTransaction(txn1);
		Set<String> names = metrics.keySet();
		for(String name : names) {
			TracedMetricData traced = metrics.get(name);
			System.out.println("Traced: name="+traced.getName()+", call count="+traced.getCallCount()+", totalTime="+traced.getTotalTimeInSec());
		}
		
		Collection<TransactionTrace> traces = introspector.getTransactionTracesForTransaction(txn1);
		System.out.println("Returned "+traces.size()+" transaction traces");
		assertEquals("Returned transaction Traces should be:", 1,traces.size());
		for(TransactionTrace trace : traces) {
			System.out.println("Trace: start="+trace.getStartTime()+", response time="+trace.getResponseTimeInSec());
			TraceSegment current = trace.getInitialTraceSegment();
			System.out.println("Initial Segment: name="+current.getName()+", method name:"+current.getMethodName()+", class name: "+current.getClassName()+", call count="+current.getCallCount());
			List<TraceSegment> children = current.getChildren();
			System.out.println("Contains "+children.size()+" children");
			int count = 0;
			while(children != null && children.size() > 0) {
				count++;
				current = children.get(0);
				System.out.println("Child Segment "+count+": name="+current.getName()+", method name:"+current.getMethodName()+", class name: "+current.getClassName()+", call count="+current.getCallCount());
				children = current.getChildren();
				System.out.println("Contains "+children.size()+" children");
			}
			
			Collection<SpanEvent> spans = introspector.getSpanEvents();
			System.out.println("There are "+spans.size()+" spans");
		}
	}
	
	@Trace(dispatcher = true)
	public void testFluxSub() {
	    System.out.println("Enter testFluxSub");
	    Flux<String> flux = getStringFlux();

	    flux.subscribeOn(Schedulers.single())
	        .subscribe(s -> System.out.println("Received: " + s));

	    flux.blockLast(); // Blocks until the Flux completes

	    System.out.println("Exit testFluxSub");
	}

	public Flux<String> getStringFlux() {
	    return Flux.just("new","relic","labs").delayElements(Duration.ofMillis(100));
	}
	
	
}
