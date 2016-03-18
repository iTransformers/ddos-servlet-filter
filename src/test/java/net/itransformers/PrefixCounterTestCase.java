/*
 * PrefixCounterTestCase.java
 *
 * Copyright [2016] [iTransformers Labs Ltd - http://itransformers.net]
 *
 * DDOS servlet filter has been created by Nikolay Milovanov and Vasil Yordanov with the purpose of defending enterprise java applications from DDOS (Distributed Denial of Service Attacks) by blackholing the attacker traffic by applying RFC rfc5635 - Remote Triggered Black Hole Filtering with Unicast Reverse Path Forwarding (uRPF)
 *
 * DDOS servlet filter has been licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.itransformers;


import org.junit.Assert;
import org.junit.Test;

public class PrefixCounterTestCase {
    @Test
    public void testCheckEnterInQuarantine(){
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // We hit too fast so that the quarantineHitThreshold is exceeded in less then hitHistoryPeriod milliseconds
        for (int i=1; i <= quarantineHitThreshold; i++) {
            final int j = i;
            boolean isInQuarantine = prefixCounter.hit(new StateChangedListener() {
                @Override
                public void stateChanged(boolean isQuarantine) {
                    if (j==quarantineHitThreshold){
                        Assert.assertTrue(isQuarantine);
                    } else {
                        throw new AssertionError("quarantine state changed");
                    }
                }
            });
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
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // lets do one hit less then quarantineHitThreshold so that we do not enter in quarantine
        for (int i=1; i <= quarantineHitThreshold-1; i++) {
            boolean isInQuarantine = prefixCounter.hit(new StateChangedListener() {
                @Override
                public void stateChanged(boolean isQuarantine) {
                    throw new AssertionError("quarantine state changed");
                }
            });
            Assert.assertFalse(isInQuarantine);
        }
    }

    @Test
    public void testCheckNotEnterInQuarantineLastHitDelayed() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Lets wait put some delay, greater than hitHistoryPeriod, on the last hit
        for (int i=1; i <= quarantineHitThreshold; i++) {
            if (i == quarantineHitThreshold - 1) {
                Thread.sleep(hitHistoryPeriod + 50);// a little bit more is better. if less sometimes the test fails
            }
            boolean isInQuarantine = prefixCounter.hit(new StateChangedListener() {
                @Override
                public void stateChanged(boolean isQuarantine) {
                    throw new AssertionError("quarantine state changed");
                }
            });
            Assert.assertFalse(isInQuarantine);
        }
    }

    @Test
    public void testCheckExitQuarantine() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Lets do last hit after delay of maxQuarantinePeriod so that we will exit from quarantine after that hit
        boolean isInQuarantine = false;
        for (int i=1; i <= quarantineHitThreshold; i++) {
            isInQuarantine = prefixCounter.hit();
        }
        Assert.assertTrue(isInQuarantine);
        Thread.sleep(maxQuarantinePeriod+50);
        isInQuarantine = prefixCounter.hit(); // TODO check state changed
        Assert.assertFalse(isInQuarantine);
    }

    @Test
    public void testCheckNotExitQuarantine() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Let do last hit before the quarantine period is over
        boolean isInQuarantine = false;
        for (int i=1; i <= quarantineHitThreshold; i++) {
            isInQuarantine = prefixCounter.hit();
        }
        Assert.assertTrue(isInQuarantine);
        Thread.sleep(maxQuarantinePeriod-100); // we hope 100 ms is enough not to exceed quarantine period
        isInQuarantine = prefixCounter.hit(new StateChangedListener() {
            @Override
            public void stateChanged(boolean isQuarantine) {
                throw new AssertionError("quarantine state changed");
            }
        });
        Assert.assertTrue(isInQuarantine);
    }

    @Test
    public void testHearBeatExitQuarantine() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Let enter in quarantine
        boolean isInQuarantine = false;
        for (int i=1; i <= quarantineHitThreshold; i++) {
            isInQuarantine = prefixCounter.hit();
        }
        Assert.assertTrue(isInQuarantine);
        for (int i=0; i < 9; i++) {
            Thread.sleep(200); // let sleep 9*200=1800 ms so that quarantine period to exceed
            isInQuarantine =  prefixCounter.heartbeat(new StateChangedListener() {
                @Override
                public void stateChanged(boolean isQuarantine) {
                        throw new AssertionError("quarantine state changed");
                }
            });
            Assert.assertTrue(isInQuarantine);
        }
        Thread.sleep(250); // lets sleep 200+50+1800=2050 ms to be sure that quarantine period is exceeded
        isInQuarantine =  prefixCounter.heartbeat(null);
        Assert.assertFalse(isInQuarantine);
    }
    @Test
    public void testHearBeatNotExitQuarantine() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        // Lets enter in quarantine
        boolean isInQuarantine = false;
        for (int i=1; i <= quarantineHitThreshold; i++) {
            isInQuarantine = prefixCounter.hit();
        }
        Assert.assertTrue(isInQuarantine);
        for (int i=0; i < 9; i++) {
            Thread.sleep(200); // let sleep 9*200=1800 ms so that quarantine period to exceed
            isInQuarantine =  prefixCounter.heartbeat();
            Assert.assertTrue(isInQuarantine);
        }
        Thread.sleep(190); // lets sleep 190+1800=1990 ms to be sure that quarantine period is not exceeded
        isInQuarantine =  prefixCounter.heartbeat(new StateChangedListener() {
            @Override
            public void stateChanged(boolean isQuarantine) {
                throw new AssertionError("quarantine state changed");
            }
        });
        Assert.assertTrue(isInQuarantine);
    }
}
