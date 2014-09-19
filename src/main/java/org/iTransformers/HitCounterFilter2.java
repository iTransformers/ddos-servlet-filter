package org.iTransformers;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class HitCounterFilter2 implements Filter {
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("\nFilter Initialization!!!");

        //Dump filter initialization parameters!!!

        System.out.println("\nTriggerPrefixLength: " + filterConfig.getInitParameter("TriggerPrefixLength"));
        System.out.println("\nPrefixHitCount: " + filterConfig.getInitParameter("PrefixHitCount"));
//        int blockingPeriod = Integer.parseInt(filterConfig.getInitParameter("BlockingPeriod"));

//        String triggerIPAddress = filterConfig.getInitParameter("TriggerIPAddress");
//        String triggerProtocol = filterConfig.getInitParameter("TriggerProtocol");
//        String userName = filterConfig.getInitParameter("UserName");
//        String userPass = filterConfig.getInitParameter("UserPass");
//        String enablePass = filterConfig.getInitParameter("EnablePass");
//        String triggerPort = filterConfig.getInitParameter("TriggerPort");
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("protocol", triggerProtocol);
//        params.put("username", userName);
//        params.put("password", userPass);
//        params.put("enable-password", enablePass);
//        params.put("address", triggerIPAddress);
//        params.put("port", triggerPort);
//        params.put("blockingPeriod",blockingPeriod);
        //Initialize prefixCounter
        HashMap<String, PrefixCounter2> prefixes = new HashMap<String, PrefixCounter2>();

        filterConfig.getServletContext().setAttribute("prefixCounter", prefixes);
//        QuarantineControl beeperControl = new QuarantineControl(prefixes,params);
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


        HashMap<String, PrefixCounter2> prefixes = (HashMap<String, PrefixCounter2>) filterConfig.getServletContext().getAttribute("prefixCounter");

        CIDRUtils utils = new CIDRUtils(remoteAddr + "/" + triggerPrefixLength);

        final String networkAddress = utils.getNetworkAddress();
        final InetAddress iPv4LocalNetMask =  utils.getIPv4LocalNetMask();
        final String subnetMask = iPv4LocalNetMask.getHostAddress();

        String prefix = remoteAddr + "/" + triggerPrefixLength;

        if (!prefixes.containsKey(prefix) ){
             prefixes.put(prefix, new PrefixCounter2(deltaPeriod, prefixHitCount, blockingPeriod));
        }

        PrefixCounter2 prefixCounter = prefixes.get(prefix);
        boolean isQuarantined = prefixCounter.hit(new StateChangedListener() {
            @Override
            public void stateChanged(boolean isQuarantine) {
                try {
                    Trigger.pullTrigger(networkAddress,subnetMask,"Null0","666");
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        if (isQuarantined) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.sendRedirect("http://cert.org");
        }

        writer.println(prefix + "->" + prefixes.get(prefix));
        writer.flush();
        filterConfig.getServletContext().log(sw.getBuffer().toString());
        chain.doFilter(request, response);
    }
}