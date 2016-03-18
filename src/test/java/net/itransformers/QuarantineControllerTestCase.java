/*
 * QuarantineControllerTestCase.java
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

import java.util.HashMap;

/**
 * Created by vasko on 9/20/14.
 */
public class QuarantineControllerTestCase {
    @Test
    public void testQuarantineControl() throws InterruptedException {
        long hitHistoryPeriod = 1000;
        final long quarantineHitThreshold = 20;
        long maxQuarantinePeriod = 2000;
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        PrefixCounter prefixCounter2 = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);

        HashMap<String, PrefixCounter> prefixes = new HashMap<String, PrefixCounter>();
        prefixes.put("192.168.1.1/24", prefixCounter);
        prefixes.put("192.168.2.1/24", prefixCounter2);

        QuarantineController quarantineController = new QuarantineController(prefixes, 1000, null, null);
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
        PrefixCounter prefixCounter = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);
        PrefixCounter prefixCounter2 = new PrefixCounter(hitHistoryPeriod, quarantineHitThreshold, maxQuarantinePeriod);

        HashMap<String, PrefixCounter> prefixes = new HashMap<String, PrefixCounter>();
        prefixes.put("192.168.1.1/24", prefixCounter);
        prefixes.put("192.168.2.1/24", prefixCounter2);

        QuarantineController quarantineController = new QuarantineController(prefixes, 1000, null, null);
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
