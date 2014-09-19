package org.iTransformers;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/1/14
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrefixCounter2 {
    private LinkedList<Long> timeticks;
    private boolean isQuarantined;
    private long hitHistoryPeriod;
    private long quarantineHitThreshold;
    private long maxQuarantinePeriod;

    public PrefixCounter2(long hitHistoryPeriod, long quarantineHitThreshold, long maxQuarantinePeriod) {
        this.hitHistoryPeriod = hitHistoryPeriod;
        this.quarantineHitThreshold = quarantineHitThreshold;
        this.maxQuarantinePeriod = maxQuarantinePeriod;
        this.timeticks = new LinkedList<Long>();
        this.isQuarantined = false;
    }

    /**
     * Hits this Prefix Counter
     * @return true if this prefix is or has just entered under quarantine
     */
    public synchronized boolean hit() {
        if (isQuarantined) {
            return hitInQuarantineState();
        } else {
            return hitInNormalState();
        }
    }

    private boolean hitInQuarantineState(){
        long now = System.currentTimeMillis();
        long last = timeticks.getLast();
        if (now - last > maxQuarantinePeriod) { //exit quarantine
            isQuarantined = false;
        }
        timeticks.clear();
        timeticks.add(now);
        return isQuarantined;
    }

    private boolean hitInNormalState(){
        long now = System.currentTimeMillis();
        updateTimeticks(now);
        int hitCounts = timeticks.size();
        if (hitCounts >= quarantineHitThreshold) { // enter in quarantine state
            timeticks.clear();
            timeticks.add(now);
            isQuarantined  = true;
        }
        return isQuarantined;
    }

    private void updateTimeticks(long millis) {
        // remove oldest time-ticks
        Iterator iter = timeticks.iterator();
        while (iter.hasNext()){
            long elementMillis =  (Long)iter.next();
            if (elementMillis + hitHistoryPeriod < millis){
                iter.remove();
            }
        }
        timeticks.add(millis);
    }
}
