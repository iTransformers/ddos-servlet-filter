package org.iTransformers;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public final class HitCounterFilter implements Filter {
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("\nFilter Initialization!!!");

        //Dump filter initialization parameters!!!

        System.out.println("\nTriggerPrefixLength: " + filterConfig.getInitParameter("TriggerPrefixLength"));
        System.out.println("\nPrefixHitCount: " + filterConfig.getInitParameter("PrefixHitCount"));
        int blockingPeriod = Integer.parseInt(filterConfig.getInitParameter("BlockingPeriod"));

        String triggerIPAddress = filterConfig.getInitParameter("TriggerIPAddress");
        String triggerProtocol = filterConfig.getInitParameter("TriggerProtocol");
        String userName = filterConfig.getInitParameter("UserName");
        String userPass = filterConfig.getInitParameter("UserPass");
        String enablePass = filterConfig.getInitParameter("EnablePass");
        String triggerPort = filterConfig.getInitParameter("TriggerPort");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("protocol", triggerProtocol);
        params.put("username", userName);
        params.put("password", userPass);
        params.put("enable-password", enablePass);
        params.put("address", triggerIPAddress);
        params.put("port", triggerPort);
        params.put("blockingPeriod",blockingPeriod);
        //Initialize prefixCounter
        Prefixes prefixes = new Prefixes();
        Thread filterThread;

        filterConfig.getServletContext().setAttribute("prefixCounter", prefixes);
        QuarantineControl beeperControl = new QuarantineControl(prefixes,params);
        beeperControl.beepForAnHour();
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request,
                         ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Filter Run:");

        if (filterConfig == null)
            return;
        String remoteAddr = request.getRemoteAddr();

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        String triggerPrefixLength = filterConfig.getInitParameter("TriggerPrefixLength");

        int prefixHitCount = Integer.parseInt(filterConfig.getInitParameter("PrefixHitCount"));

        int blockingPeriod = Integer.parseInt(filterConfig.getInitParameter("BlockingPeriod"));

        long deltaPeriod = Integer.parseInt(filterConfig.getInitParameter("DeltaPeriod"));


        Prefixes prefixes = (Prefixes) filterConfig.getServletContext().getAttribute("prefixCounter");

        long currentTimeMillis = System.currentTimeMillis();
        CIDRUtils utils = new CIDRUtils(remoteAddr + "/" + triggerPrefixLength);

        String networkAddress = utils.getNetworkAddress();
        InetAddress iPv4LocalNetMask =  utils.getIPv4LocalNetMask();
        String subnetMask = iPv4LocalNetMask.getHostAddress();

        String prefix = remoteAddr + "/" + triggerPrefixLength;

        if (prefixes.getPrefix(prefix) != null) {
            PrefixCounter prefixCounter = prefixes.getPrefix(prefix);

            if (!prefixes.getPrefix(prefix).isQuarantined()) {
                int counter = prefixCounter.getHitCounter();
                if (counter >= prefixHitCount) {
                    System.out.println("Pull the trigger!!!");
                    try {
                        Trigger.pullTrigger(networkAddress,subnetMask,"Null0","666");
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    prefixCounter.setQuarantined(true);
                    prefixCounter.deleteMillis(currentTimeMillis);

                    //  prefixes.updatePrefixQuarantineStatus(prefix,true);
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.sendRedirect("http://cert.org");
                } else {
                    prefixCounter.updatedMillis(currentTimeMillis, deltaPeriod);
                    //prefixCounter.setHitCounter(--counter);
                }
            } else if (!prefixCounter.checkQuarantineStatus(currentTimeMillis, blockingPeriod)) {
//                System.out.println("Quarantine End!");
//                try {
//                    Trigger.pullOffTrigger(networkAddress,subnetMask,"Null0","666");
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
                //blockingPeriod++;
            //    filterConfig.getServletContext().setAttribute("blockingPeriod", ++blockingPeriod);
            //    System.out.println("Increasing the blocking period = "+blockingPeriod);
            } else {
                System.out.println("Still in Quarantine!");
                HttpServletResponse httpResponse = (HttpServletResponse) response;

                httpResponse.sendError(499);

            }
        } else {
            prefixes.addPrefix(prefix, currentTimeMillis, false);
        }

        writer.println(prefix + "->" + prefixes.getPrefix(prefix).getHitCounter());
        writer.flush();
        filterConfig.getServletContext().log(sw.getBuffer().toString());
        chain.doFilter(request, response);
    }
}