package org.iTransformers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


class QuarantineControl {
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   Prefixes prefixes;
   int blockingPeriod;


    QuarantineControl(Prefixes prefixes,int blockingPeriod) {
        this.prefixes = prefixes;
        this.blockingPeriod = blockingPeriod;
    }


    public void beepForAnHour() {
     final Runnable beeper = new Runnable() {
       public void run() {
           System.out.println("beep");
           if(prefixes.getPrefixes()!=null){
               Iterator it = prefixes.getPrefixes().entrySet().iterator();
               while (it.hasNext()){
                   System.out.println(it.getClass());
                   long currentTimeMillis = System.currentTimeMillis();
                   HashMap <String, PrefixCounter> prefix = (HashMap<String, PrefixCounter>) it.next();

    //               if(!prefixCounter.checkQuarantineStatus(currentTimeMillis,blockingPeriod )){
    //
    //                   Trigger.pullOffTrigger(networkAddress,subnetMask,"Null0","666");
    //
    //               }


               }

           }
       }
     };


       final ScheduledFuture<?> beeperHandle =
       scheduler.scheduleAtFixedRate(beeper, 100, 100, SECONDS);
     scheduler.schedule(new Runnable() {
       public void run() { beeperHandle.cancel(true); }
     }, 60 * 60, SECONDS);
   }
 }