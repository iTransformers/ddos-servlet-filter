package org.iTransformers;


import org.junit.Assert;
import org.junit.Test;

public class PrefixCounter2TestCase {
    @Test
    public void testCheckEnterInQuarantine(){
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // We hit too fast so that the quarantineHitThreshold is exceeded in less then hitHistoryPeriod milliseconds
        for (int i=1; i <= quarantineHitThreshold; i++) {
            boolean isInQuarantine = prefixCounter.hit();
            if (i==quarantineHitThreshold) {
                Assert.assertTrue(isInQuarantine);
            } else {
                Assert.assertFalse( isInQuarantine);
            }
        }
    }

    @Test
    public void testCheckNotEnterInQuarantineSmallAmountOfHits() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // lets do one hit less then quarantineHitThreshold so that we do not enter in quarantine
        for (int i=1; i <= quarantineHitThreshold-1; i++) {
            boolean isInQuarantine = prefixCounter.hit();
            Assert.assertFalse(isInQuarantine);
        }
    }

    @Test
    public void testCheckNotEnterInQuarantineLastHitDelayed() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Lets wait put some delay, greater than hitHistoryPeriod, on the last hit
        for (int i=1; i <= quarantineHitThreshold; i++) {
            if (i == quarantineHitThreshold -1) {
                Thread.sleep(hitHistoryPeriod+1);
            }
            boolean isInQuarantine = prefixCounter.hit();
            if (i==quarantineHitThreshold) {
                Assert.assertFalse(isInQuarantine);
            } else {
                Assert.assertFalse(isInQuarantine);
            }
        }
    }

    @Test
    public void testCheckExitQuarantine() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Lets do last hit after delay of maxQuarantinePeriod so that we will exit from quarantine after that hit
        boolean isInQuarantine = false;
        for (int i=1; i <= quarantineHitThreshold; i++) {
            isInQuarantine = prefixCounter.hit();
        }
        Assert.assertTrue(isInQuarantine);
        Thread.sleep(maxQuarantinePeriod+1);
        isInQuarantine = prefixCounter.hit();
        Assert.assertFalse(isInQuarantine);
    }

    @Test
    public void testCheckNotExitQuarantine() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter2 prefixCounter = new PrefixCounter2(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Let do last hit before the quarantine period is over
        boolean isInQuarantine = false;
        for (int i=1; i <= quarantineHitThreshold; i++) {
            isInQuarantine = prefixCounter.hit();
        }
        Assert.assertTrue(isInQuarantine);
        Thread.sleep(maxQuarantinePeriod-100); // we hope 100 ms is enough not to exceed quarantine period
        isInQuarantine = prefixCounter.hit();
        Assert.assertTrue(isInQuarantine);
    }
}
