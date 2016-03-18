/*
 * QuarantineController.java
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

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class QuarantineController implements Runnable{

    private HashMap<String, PrefixCounter> prefixes;
    private long quarantineCheckTime;
    private Map<String, Object> params;
    private QuarantineControllerAction action;
    private boolean running = true;

    public QuarantineController(HashMap<String, PrefixCounter> prefixes, long quarantineCheckTime, Map<String, Object> params, QuarantineControllerAction action) {
        this.prefixes = prefixes;
        this.quarantineCheckTime = quarantineCheckTime;
        this.params = params;
        this.action = action;
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
                            if (action != null) {
                                action.exitQuarantine(prefix, params);
                            } else {
                                System.out.println("Quarantine Controller Action is null skiping it");
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