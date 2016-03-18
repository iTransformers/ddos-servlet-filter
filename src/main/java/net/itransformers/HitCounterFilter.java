/*
 * HitCounterFilter.java
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

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public final class HitCounterFilter implements Filter {
    private FilterConfig filterConfig = null;
    private QuarantineController quarantineController;
    private QuarantineControllerAction triger = new Trigger();
    Map<String, Object> params = new HashMap<String, Object>();

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("\nFilter Initialization!!!");

        int blockingPeriod = Integer.parseInt(filterConfig.getInitParameter("BlockingPeriod"));

        params.put("protocol", filterConfig.getInitParameter("TriggerProtocol"));
        params.put("username", filterConfig.getInitParameter("UserName"));
        params.put("password", filterConfig.getInitParameter("UserPass"));
        params.put("enable-password", filterConfig.getInitParameter("EnablePass"));
        params.put("address", filterConfig.getInitParameter("TriggerIPAddress"));
        params.put("port", Integer.parseInt(filterConfig.getInitParameter("TriggerPort")));
        params.put("timeout", Integer.parseInt(filterConfig.getInitParameter("sshTimeout")));

        //params.put("blockingPeriod",blockingPeriod);
//        Initialize prefixCounter
        HashMap<String, PrefixCounter> prefixes = new HashMap<String, PrefixCounter>();

        filterConfig.getServletContext().setAttribute("prefixCounter", prefixes);
      //  filterConfig.getServletContext().setAttribute("params, params);

        quarantineController = new QuarantineController(prefixes, blockingPeriod, params, triger);
        quarantineController.start();
    }

    public void destroy() {
        quarantineController.stop();
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request,
                         ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("Filter Run:");

        if (filterConfig == null) return;

        String remoteAddr = request.getRemoteAddr();

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        String triggerPrefixLength = filterConfig.getInitParameter("TriggerPrefixLength");

        int prefixHitCount = Integer.parseInt(filterConfig.getInitParameter("PrefixHitCount"));
        int blockingPeriod = Integer.parseInt(filterConfig.getInitParameter("BlockingPeriod"));
        long deltaPeriod = Integer.parseInt(filterConfig.getInitParameter("DeltaPeriod"));

        HashMap<String, PrefixCounter> prefixes = (HashMap<String, PrefixCounter>) filterConfig.getServletContext().getAttribute("prefixCounter");

        final String prefix = remoteAddr + "/" + triggerPrefixLength;

        if (!prefixes.containsKey(prefix) ){
             prefixes.put(prefix, new PrefixCounter(deltaPeriod, prefixHitCount, blockingPeriod));
        }

        final PrefixCounter prefixCounter = prefixes.get(prefix);
        //Prefix counter will pull the trigger if the trigger for that prefix is not already pulled!
        //If trigger is pulled but we still get a request we will reset it and will just encounter one more hit.
        //This will change the time in which the prefix will exit from quarantine!
        prefixCounter.hit(new StateChangedListener() {
            @Override
            public void stateChanged(boolean isQuarantine) {
                triger.enterQuarantine(prefix, params);
            }
        });
        writer.println(prefix + "->" + prefixes.get(prefix));
        filterConfig.getServletContext().log(sw.getBuffer().toString());
        chain.doFilter(request, response);
    }
}