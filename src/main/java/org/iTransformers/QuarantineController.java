package org.iTransformers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class QuarantineController implements Runnable{

    private HashMap<String, PrefixCounter> prefixes;
    private long quarantineCheckTime;
    private Map<String, Object> params;
    private QuarantineControllerAction action;
    private boolean running = true;

    public QuarantineController(HashMap<String, PrefixCounter> prefixes, long quarantineCheckTime, Map<String, Object> params, QuarantineControllerAction action) {
        this.prefixes = prefixes;
        this.quarantineCheckTime = quarantineCheckTime;
        this.params = params;
        this.action = action;
    }
    public void start(){
        (new Thread(this)).start();
    }

    @Override
    public synchronized void run() {
        while (running){
            for (final String prefix : prefixes.keySet()) {
                final PrefixCounter prefixCounter =prefixes.get(prefix);
                prefixCounter.heartbeat(new StateChangedListener() {
                    @Override
                    public void stateChanged(boolean isQuarantine) {
                        if (!isQuarantine) {
                            if (action != null) {
                                action.exitQuarantine(prefix, params);
                            } else {
                                System.out.println("Quarantine Controller Action is null skiping it");
                            }
                        }   else{
                            System.out.println("Prefix "+prefix+ " is still in quarantine!!!");

                        }
                    }
                });
            }
            try {
                wait(quarantineCheckTime);
                System.out.println("Quarantine control is awake! Checking prefixes for expired quarantines");

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