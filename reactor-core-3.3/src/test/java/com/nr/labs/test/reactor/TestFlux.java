package com.nr.labs.test.reactor;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;

public class TestFlux {

    private static final Random random = new Random();


    private static Integer[] randomIntegerList() {
        int n = random.nextInt(10) + 1;
        Set<Integer> set = new HashSet<>();

        while(set.size() < n) {
            Integer i = random.nextInt(100);
            if(!set.contains(i)) {
                set.add(i);
            }
        }
        Integer[] array = new Integer[10];
        set.toArray(array);
        return array;
    }

    public static void main(String[] args) {
        TestFlux test = new TestFlux();
        test.test();
    }


    public void test() {

        Flux<Integer> initial = getFluxFromStream();

        CompletableFuture<Boolean> done = new CompletableFuture<Boolean>();

        Subscription sub = new Subscription() {

            @Override
            public void request(long n) {
                // TODO Auto-generated method stub

            }

            @Override
            public void cancel() {
                // TODO Auto-generated method stub

            }
        };


        Flux<Integer> flux = initial.map(s -> {
            pause();
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

        flux.subscribe(System.out::println);
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
        pause(4000L);
    }

    private Flux<Integer> getFluxFromStream() {
        Integer[] numbers = randomIntegerList();
        Stream<Integer> stream = Stream.of(numbers);
        return Flux.fromStream(stream);
    }

    private static void pause() {
        System.out.println("Call to pause");
        int n = random.nextInt(5);
        pause(n*50L);
    }

    private static void pause(long ms) {
        if(ms > 0) {
            System.out.println("Pausing for " + ms + " ms");
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
