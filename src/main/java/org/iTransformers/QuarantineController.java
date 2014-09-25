package org.iTransformers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class QuarantineController implements Runnable{

    private HashMap<String, PrefixCounter> prefixes;
    private long quarantineCheckTime;
    private Map<String, Object> params;
    private boolean running = true;

    public QuarantineController(HashMap<String, PrefixCounter> prefixes, long quarantineCheckTime, Map<String, Object> params) {
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
                final PrefixCounter prefixCounter =prefixes.get(prefix);
                prefixCounter.heartbeat(new StateChangedListener() {
                    @Override
                    public void stateChanged(boolean isQuarantine) {
                        if (!isQuarantine) {
                            System.out.println("Prefix "+ prefix +" quarantine has finished. So let's pull off the trigger!!!");
                            try {
                                CIDRUtils utils = new CIDRUtils(prefix);


                            Trigger.pullOffTrigger(utils.getNetworkAddress(), utils.getIPv4LocalNetMask().getHostAddress(), "Null0", "666", params);
                                // TODO handle exceptions in a better way!!!
                            prefixCounter.setPulled(false);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
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