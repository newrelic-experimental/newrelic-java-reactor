package com.nr.labs.test.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Await {

    private String result = null;
    private final CompletableFuture<String> f;

    public Await() {
        f = new CompletableFuture<String>();
    }

    public String  await() {
        String s = null;
        try {
            s = f.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void setResult(String s) {
        result = s;
        f.complete(result);
    }
}