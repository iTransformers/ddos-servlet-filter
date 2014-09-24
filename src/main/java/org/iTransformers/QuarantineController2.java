package org.iTransformers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class QuarantineController2 implements Runnable{

    private HashMap<String, PrefixCounter2> prefixes;
    private long quarantineCheckTime;
    private Map<String, Object> params;
    private boolean running = true;

    public QuarantineController2(HashMap<String, PrefixCounter2> prefixes, long quarantineCheckTime, Map<String, Object> param) {
        this.prefixes = prefixes;
        this.quarantineCheckTime = quarantineCheckTime;
        this.params = params;
    }
    public void start(){
        (new Thread(this)).start();
    }

    @Override
    public synchronized void run() {
        while (running){
            for (final String prefix : prefixes.keySet()) {
                PrefixCounter2 prefixCounter =prefixes.get(prefix);
                prefixCounter.heartbeat(new StateChangedListener() {
                    @Override
                    public void stateChanged(boolean isQuarantine) {
                        if (isQuarantine) {
                            System.out.println("Prefix "+ prefix +" quarantine has finished. So let's pull off the trigger!!!");
                            try {
                                CIDRUtils utils = new CIDRUtils(prefix);


                            Trigger.pullTrigger(utils.getNetworkAddress(),utils.getIPv4LocalNetMask().getHostAddress(),"Null0","666",params);
                                // TODO handle exceptions in a better way!!!

                            } catch (UnknownHostException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (Exception e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                        }   else{
                            System.out.println("Prefix "+prefix+ " is still in quarantine!!!");

                        }
                    }
                });
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