package com.nr.labs.test.reactor;

import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.Introspector;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.newrelic.agent.introspec.InstrumentationTestRunner;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Scheduler.Worker;
import reactor.core.scheduler.Schedulers;

import static org.junit.Assert.assertEquals;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = {"reactor"})
public class ReactorTest {

    private static final String[] colors = new String[] {"blue","red","yellow","black","green","white","pink","purple","gray","orange","violet","indigo","beige","brown"};
    private static final Random random = new Random();

    private int numberOfItems;

    @Test
    public void testMonoPublishOn() {
        Introspector introspector = InstrumentationTestRunner.getIntrospector();
        testMonoPubOn();
        System.out.println("testMonoPublishOn finished");
        int count = introspector.getFinishedTransactionCount(7000);
        System.out.println("Introspector.getFinishedTransactionCount: " + count);
        //assertEquals(1, count);

        Collection<String> txnNames = introspector.getTransactionNames();

        System.out.println("Found " +  txnNames.size() + " transactions" );
        for(String txnName : txnNames) {
            System.out.println("txnName: " + txnName);
        }
    }

    @Test
    public void monoSyncTest() {
        Introspector introspector = InstrumentationTestRunner.getIntrospector();
        testMonoSync();
        System.out.println("testMonoSync finished");
        int count = introspector.getFinishedTransactionCount(7000);
        System.out.println("Introspector.getFinishedTransactionCount: " + count);
        //assertEquals(1, count);

        Collection<String> txnNames = introspector.getTransactionNames();

        System.out.println("Found " +  txnNames.size() + " transactions" );
        for(String txnName : txnNames) {
            System.out.println("txnName: " + txnName);
        }
    }

    @Trace(dispatcher = true)
    public void testMonoSync() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Mono","Sync");
        System.out.println("call to testMonoSync");
        Mono<String> mono = getCallableMono();
        mono = mono.doOnError( new ErrorConsumer()).doOnNext(new StringConsumer("MonoConsumer")).doOnSubscribe(new SubscriptionConsumer()).doOnSuccess(new StringConsumer("OnSuccess"));
        Await await = new Await();
        mono.subscribe(new TestCoreSubscriber(await));

        await.await();
        System.out.println("testMonoSync is done");
    }

    @Trace(dispatcher = true)
    public void testMonoSubOn() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Mono","SubOn");
        System.out.println("call to testMonoSubOn");
        Mono<String> mono = getCallableMono();
        mono = mono.doOnError( new ErrorConsumer()).doOnNext(new StringConsumer("MonoConsumer")).doOnSubscribe(new SubscriptionConsumer()).doOnSuccess(new StringConsumer("OnSuccess"));
        Await await = new Await();
        mono.subscribeOn(Schedulers.single()).subscribe(new TestCoreSubscriber(await));
        await.await();
        System.out.println("testMonoSubOn is done");
    }

    @Trace(dispatcher = true)
    public void testMonoPubOn() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Mono","PubOn");
        System.out.println("call to testMonoPubOn");
        Mono<String> mono = getCallableMono();
        mono = mono.doOnError( new ErrorConsumer()).doOnNext(new StringConsumer("MonoConsumer")).doOnSubscribe(new SubscriptionConsumer()).doOnSuccess(new StringConsumer("OnSuccess"));
        Await await = new Await();
        mono.publishOn(Schedulers.single()).subscribe(new TestCoreSubscriber(await));
        await.await();
        System.out.println("testMonoPubOn is done");
    }

    @Trace(dispatcher = true)
    public void testMonoPubOnSubOn() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Mono","SubOnPubOn");
        System.out.println("call to testMonoPubOn");
        Mono<String> mono = getCallableMono();
        mono = mono.doOnError( new ErrorConsumer()).doOnNext(new StringConsumer("MonoConsumer")).doOnSubscribe(new SubscriptionConsumer()).doOnSuccess(new StringConsumer("OnSuccess"));
        Await await = new Await();
        mono.publishOn(Schedulers.single()).subscribeOn(Schedulers.single()).subscribe(new TestCoreSubscriber(await));
        await.await();
        System.out.println("testMonoPubOn is done");
    }

    @Trace(dispatcher = true)
    public void testFluxSync() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Flux","Sync");
        System.out.println("call to testFluxSync");
        Flux<String> flux = getArrayFlux();
        flux = flux.doOnNext(new StringConsumer("FluxOnNextConsumer")).doOnError(new ErrorConsumer()).doOnSubscribe(new SubscriptionConsumer()).doOnComplete(new CompletionRunnable());
        Await await = new Await();

        flux.subscribe(new TestCoreSubscriber2(await,numberOfItems));

        await.await();
        System.out.println("testFluxSync has finished");
    }


    @Trace(dispatcher = true)
    public void testFluxSubOn() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Flux","SubOn");
        System.out.println("call to testFluxSubOn");
        Flux<String> flux = getArrayFlux();
        flux = flux.doOnNext(new StringConsumer("FluxOnNextConsumer")).doOnError(new ErrorConsumer()).doOnSubscribe(new SubscriptionConsumer()).doOnComplete(new CompletionRunnable());
        Await await = new Await();

        flux.subscribeOn(Schedulers.single()).subscribe(new TestCoreSubscriber2(await,numberOfItems));

        await.await();
        System.out.println("testFluxSubOn has finished");
    }

    @Trace(dispatcher = true)
    public void testFluxPubOn() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Flux","PubOn");
        System.out.println("call to testFluxPubOn");
        Flux<String> flux = getArrayFlux();
        flux = flux.doOnNext(new StringConsumer("FluxOnNextConsumer")).doOnError(new ErrorConsumer()).doOnSubscribe(new SubscriptionConsumer()).doOnComplete(new CompletionRunnable());
        Await await = new Await();

        flux.publishOn(Schedulers.single()).subscribe(new TestCoreSubscriber2(await,numberOfItems));

        await.await();
        System.out.println("testFluxPubOn has finished");
    }

    @Trace(dispatcher = true)
    public void testFluxPubOnSubOn() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Flux","SubOnPubOn");
        System.out.println("call to testFluxPubOn");
        Flux<String> flux = getArrayFlux();
        flux = flux.doOnNext(new StringConsumer("FluxOnNextConsumer")).doOnError(new ErrorConsumer()).doOnSubscribe(new SubscriptionConsumer()).doOnComplete(new CompletionRunnable());
        Await await = new Await();

        flux.publishOn(Schedulers.single()).subscribeOn(Schedulers.single(), true).subscribe(new TestCoreSubscriber2(await,numberOfItems));

        await.await();
        System.out.println("testFluxPubOn has finished");
    }


    @Trace(dispatcher = true)
    public void testFluxSink() {

        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Flux","Sink");
        EventProducer producer = new EventProducer();

        Flux<String> flux = Flux.create(sink -> {
            // Store the sink for later use in another thread or callback
            producer.registerListener(new EventListener() {
                @Override
                @Trace(dispatcher = true)
                public void onEvent(String event) {
                    sink.next(event); // Emit the next item
                }
                @Override
                @Trace(dispatcher = true)
                public void onComplete() {
                    sink.complete(); // Signal completion
                }
                @Override
                @Trace(dispatcher = true)
                public void onError(Throwable error) {
                    sink.error(error); // Signal an error
                }
            });
        });

        flux.subscribe(
                System.out::println,           // onNext consumer
                error -> System.err.println("Error: " + error), // onError consumer
                () -> System.out.println("Completed!")         // onComplete runnable
        );

        // Simulate external events being produced
        producer.simulateEvents();

        //		Flux<String> eventStream = Flux.create(fluxSink -> {
        //
        //			fluxSink.next("Event 1");
        //			fluxSink.next("Event 2");
        //			fluxSink.next("Event 3");
        //
        //			new Thread(() -> {
        //				try {
        //					Thread.sleep(1000);
        //					fluxSink.next("Async Event");
        //					fluxSink.complete();
        //				} catch(InterruptedException e) {
        //					fluxSink.error(e);
        //				}
        //			}).start();
        //		});
        //
        //		eventStream = eventStream.delayElements(Duration.ofMillis(50L)).doOnNext(new StringConsumer("FluxOnNextConsumer")).doOnError(new ErrorConsumer()).doOnSubscribe(new SubscriptionConsumer()).doOnComplete(new CompletionRunnable());
        //		Await await = new Await();
        //
        //		eventStream.subscribe(new TestCoreSubscriber2(await,3));
        //
        //		await.await();
        System.out.println("testFluxSink has finished");



    }

    @Trace(dispatcher = true)
    public void testMonoSink() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Mono","Sink");

        Mono<String> delayedMono = Mono.create(sink -> {
            // Schedule an operation to emit a value after a delay
            Scheduler scheduler = Schedulers.single();
            scheduler.schedule(() -> {
                System.out.println("Emitting value from MonoSink...");
                sink.success("Hello from MonoSink!"); // Emit the value and complete
                scheduler.dispose();
            }, 2, TimeUnit.SECONDS); // Delay for 2 seconds

            // Optional: Handle cancellation
            sink.onDispose(() -> {
                System.out.println("MonoSink disposed (e.g., subscriber cancelled)");
                scheduler.dispose();
            });
        });

        delayedMono = delayedMono.doOnError( new ErrorConsumer()).doOnNext(new StringConsumer("MonoConsumer")).doOnSubscribe(new SubscriptionConsumer()).doOnSuccess(new StringConsumer("OnSuccess"));
        Await await = new Await();
        delayedMono.subscribe(new TestCoreSubscriber(await));

        await.await();
        System.out.println("testMonoSink is done");


    }

    @Trace(dispatcher = true)
    public void testSingleScheduler() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Schedule","Single");
        Scheduler singleScheduler = Schedulers.single();

        Worker singleWorker = singleScheduler.createWorker();

        // Submit to schedule
        singleScheduler.schedule(new ScheduleRunnable("Single Schedule"));

        // submit to worker
        singleWorker.schedule(new ScheduleRunnable("Single Schedule Worker"));

    }

    @Trace(dispatcher = true)
    public void testElasticScheduler() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Schedule","Elastic");
        Scheduler elasticScheduler = Schedulers.elastic();

        Worker elasticWorker = elasticScheduler.createWorker();

        // Submit to schedule
        elasticScheduler.schedule(new ScheduleRunnable("Elastic Schedule"));

        // submit to worker
        elasticWorker.schedule(new ScheduleRunnable("Elastic Schedule Worker"));


    }

    @Trace(dispatcher = true)
    public void testParallelScheduler() {
        NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Reactor", "Reactor","Schedule","Parallel");
        Scheduler parallelScheduler = Schedulers.parallel();

        Worker parallelWorker = parallelScheduler.createWorker();

        // Submit to schedule
        parallelScheduler.schedule(new ScheduleRunnable("Parallel Schedule"));

        // submit to worker
        parallelWorker.schedule(new ScheduleRunnable("Parallel Schedule Worker"));


    }

    private Mono<String> getCallableMono() {
        return Mono.fromCallable(new Callable<String>() {

            @Override
            @Trace(dispatcher = true)
            public String call() throws Exception {
                int n = random.nextInt(colors.length);
                System.out.println("Returning color: "+colors[n]);
                return colors[n];
            }
        });
    }

    private Flux<String> getArrayFlux() {
        int n = random.nextInt(colors.length) + 1;
        String[] colorArray = new String[n];
        for(int i=0;i<n;i++) {
            int index = random.nextInt(colors.length);
            colorArray[i] = colors[index];
        }
        numberOfItems = n;

        return Flux.fromArray(colorArray);
    }

    private class ScheduleRunnable implements Runnable {

        private String name = null;

        public ScheduleRunnable(String n) {
            name = n;
        }

        @Override
        @Trace
        public void run() {
            NewRelic.getAgent().getTracedMethod().setMetricName("Custom","ScheduleRunnable",name);
            System.out.println("Entering " + name + " Runnable");
        }

    }

    // Helper classes to simulate an external event source
    class EventProducer {
        private EventListener listener;

        public void registerListener(EventListener eventListener) {
            listener = eventListener;
        }

        public void simulateEvents() {
            if (listener != null) {
                new Thread(() -> {
                    PauseService.pause(100L);
                    listener.onEvent("Event 1");
                    PauseService.pause(100L);
                    listener.onEvent("Event 2");
                    PauseService.pause(100L);
                    listener.onEvent("Event 3");
                    PauseService.pause(100L);
                    listener.onComplete();
                }).start();
            }
        }
    }

    interface EventListener {
        void onEvent(String event);
        void onComplete();
        void onError(Throwable error);
    }
}
