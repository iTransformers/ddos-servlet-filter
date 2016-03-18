/*
 * StateChangedListener.java
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


public interface StateChangedListener {
    public void stateChanged(boolean isQuarantine);
}
