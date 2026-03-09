package com.nr.labs.test.reactor;

import java.util.Random;

public class PauseService {

    private static final Random random = new Random();
    private static final int MAX_UNITS = 15;
    private static final long UNIT = 100L;
    private static final long SECOND = 1000L;
    private static final int MAX_SECONDS = 10;

    public static int randomInteger(int bound) {
        return random.nextInt(bound);
    }

    public static void pauseRandomUnits() {
        int n = random.nextInt(MAX_UNITS);
        pause(n*UNIT);
    }

    public static void pauseRandomSeconds() {
        int n = random.nextInt(MAX_SECONDS);
        pause(n*SECOND);
    }

    public static void pauseSeconds(int n) {
        pause(n*SECOND);
    }

    public static void pause(long ms) {
        System.out.println("Pausing for " + ms + " ms");
        if(ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
