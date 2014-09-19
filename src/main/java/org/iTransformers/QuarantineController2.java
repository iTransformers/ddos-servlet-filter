package org.iTransformers;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


class QuarantineController2 {
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   Prefixes prefixes;
    Map<String, Object> params;

    QuarantineController2(Prefixes prefixes, Map<String, Object> params) {
        this.prefixes = prefixes;
        this.params = params;
    }



    public void beepForAnHour() {
     final Runnable beeper = new Runnable() {
       public void run() {
           System.out.println("beep");
           if(prefixes.getPrefixes().entrySet()!=null){
               Iterator it = prefixes.getPrefixes().entrySet().iterator();


               while (it.hasNext()){
                   long currentTimeMillis = System.currentTimeMillis();
                   System.out.println(it.next().getClass());
//                   HashMap <String, PrefixCounter> prefix = (HashMap<String, PrefixCounter>) it.next();

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