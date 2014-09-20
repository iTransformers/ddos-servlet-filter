package org.iTransformers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


public class QuarantineController2 implements Runnable{

    private HashMap<String, PrefixCounter2> prefixes;
    private long quarantineCheckTime;
    private boolean running = true;

    public QuarantineController2(HashMap<String, PrefixCounter2> prefixes, long quarantineCheckTime) {
        this.prefixes = prefixes;
        this.quarantineCheckTime = quarantineCheckTime;
    }
    public void start(){
        (new Thread(this)).start();
    }

    @Override
    public synchronized void run() {
        while (running){
            for (PrefixCounter2 prefixCounter2 : prefixes.values()) {
                prefixCounter2.heartbeat();
            }
            try {
                wait(quarantineCheckTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public synchronized void stop(){
        running = false;
        notifyAll();
    }
}