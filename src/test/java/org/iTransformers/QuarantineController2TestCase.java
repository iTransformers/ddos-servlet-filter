package org.iTransformers;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by vasko on 9/20/14.
 */
public class QuarantineController2TestCase {
    @Test
    public void testQuarantineControl() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        PrefixCounter2 prefixCounter2 = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);

        HashMap<String, PrefixCounter2> prefixes = new HashMap<String, PrefixCounter2>();
        prefixes.put("123", prefixCounter);
        prefixes.put("1234", prefixCounter2);

        QuarantineController2 quarantineController = new QuarantineController2(prefixes, 1000, null);
        quarantineController.start();
        boolean isInQuarantine = false;
        boolean isInQuarantine2 = false;
        // lets enter the two prefixes into quarantine
        for (int i=1; i <= quarantineHitThreshold; i++){
            isInQuarantine = prefixCounter.hit();
            isInQuarantine2 = prefixCounter2.hit();
        }
        Assert.assertTrue(isInQuarantine);
        Assert.assertTrue(isInQuarantine2);
        Thread.sleep(2050);
        isInQuarantine = prefixCounter.hit();
        isInQuarantine2 = prefixCounter.hit();
        Assert.assertFalse(isInQuarantine);
        Assert.assertFalse(isInQuarantine2);
        quarantineController.stop();
    }

    @Test
    public void testQuarantineControlNotExitQuarantines() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        PrefixCounter2 prefixCounter2 = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);

        HashMap<String, PrefixCounter2> prefixes = new HashMap<String, PrefixCounter2>();
        prefixes.put("123", prefixCounter);
        prefixes.put("1234", prefixCounter2);

        QuarantineController2 quarantineController = new QuarantineController2(prefixes, 1000, null);
        quarantineController.start();
        boolean isInQuarantine = false;
        boolean isInQuarantine2 = false;
        // lets enter the two prefixes into quarantine
        for (int i=1; i <= quarantineHitThreshold; i++){
            isInQuarantine = prefixCounter.hit();
            isInQuarantine2 = prefixCounter2.hit();
        }
        Assert.assertTrue(isInQuarantine);
        Assert.assertTrue(isInQuarantine2);
        Thread.sleep(1950); // less than 2000 ms
        isInQuarantine = prefixCounter.hit();
        isInQuarantine2 = prefixCounter.hit();
        Assert.assertTrue(isInQuarantine);
        Assert.assertTrue(isInQuarantine2);
        quarantineController.stop();
    }
}
