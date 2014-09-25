package org.iTransformers;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/1/14
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrefixCounter {
    private LinkedList<Long> timeticks;
    private boolean isQuarantined;
    private long hitHistoryPeriod;
    private long quarantineHitThreshold;
    private long maxQuarantinePeriod;

    public PrefixCounter(long hitHistoryPeriod, long quarantineHitThreshold, long maxQuarantinePeriod) {
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
        return hit(null);
    }

    public synchronized boolean hit(StateChangedListener enterQuarantineListener) {
        if (isQuarantined) {
            return hitInQuarantineState(enterQuarantineListener);
        } else {
            return hitInNormalState(enterQuarantineListener);
        }
    }

    public synchronized  boolean heartbeat(){
        return heartbeat(null);
    }

    public synchronized  boolean heartbeat(StateChangedListener enterQuarantineListener){
        if (isQuarantined) {
            long now = System.currentTimeMillis();
            refreshQuarantine(enterQuarantineListener, now);
            if (!isQuarantined){
                timeticks.clear();
                timeticks.add(now);
            }
        }
        return isQuarantined;
    }

    private boolean hitInQuarantineState(StateChangedListener enterQuarantineListener){
        long now = System.currentTimeMillis();
        refreshQuarantine(enterQuarantineListener, now);
        timeticks.clear();
        timeticks.add(now);
        return isQuarantined;
    }

    private boolean hitInNormalState(StateChangedListener enterQuarantineListener){
        long now = System.currentTimeMillis();
        updateTimeticks(now);
        int hitCounts = timeticks.size();
        if (hitCounts >= quarantineHitThreshold) { // enter in quarantine state
            timeticks.clear();
            timeticks.add(now);
            isQuarantined  = true;
            fireStateChangedEvent(enterQuarantineListener);
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

    private void refreshQuarantine(StateChangedListener enterQuarantineListener, long now) {
        long last = timeticks.getLast();
        if (now - last > maxQuarantinePeriod) { //exit quarantine
            isQuarantined = false;
            fireStateChangedEvent(enterQuarantineListener);
        }
    }

    /**
     * Fires state changed event in new thread
     * @param enterQuarantineListener the listener to which the event will be fired
     */
    private void fireStateChangedEvent(final StateChangedListener enterQuarantineListener){
        final boolean isQuarantined = this.isQuarantined;
        if (enterQuarantineListener != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    enterQuarantineListener.stateChanged(isQuarantined);
                }
            }).start();
        }
    }
}
