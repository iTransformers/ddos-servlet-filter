package org.iTransformers;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/1/14
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrefixCounter {
    HashSet<Long> timeticks;
    boolean quarantined;

    public PrefixCounter() {
        this.timeticks = new HashSet<Long>();
        this.quarantined = false;
    }

    public int getHitCounter() {
        return timeticks.size();
    }

    public void addMillis(long millis) {
        this.timeticks.add(millis);
    }
    public void updatedMillis(long millis, long delta) {
        Iterator iter = timeticks.iterator();
        while (iter.hasNext()){
            long elementMillis =  (Long)iter.next();
            long oldestMillis = millis - delta;
            if (elementMillis < oldestMillis){
                iter.remove();
            }
        }
        timeticks.add(millis);
    }
    public boolean isQuarantined() {
        return quarantined;
    }

    public void setQuarantined(boolean quarantined) {
        this.quarantined = quarantined;
    }

    public void deleteMillis(long currentTimeMillis) {
        timeticks.clear();
        timeticks.add(currentTimeMillis);
    }

    @Override
    public String toString() {
        return "PrefixCounters{" +
                timeticks.size()+
                ", timeticks=" + timeticks +
                ", quarantined=" + quarantined +
                '}';
    }

    public boolean checkQuarantineStatus(long currentTimeMillis,long blockingPeriod) {
        Iterator iter = timeticks.iterator();
        while (iter.hasNext()){
            long elementMillis =  (Long)iter.next();
            long quarantineEnd = elementMillis+ blockingPeriod;
                if (quarantineEnd < currentTimeMillis){
                    setQuarantined(false);
                    timeticks.clear();
                    timeticks.add(currentTimeMillis);
                    return false;
                }
        }
        return true;

        //To change body of created methods use File | Settings | File Templates.
    }
}
