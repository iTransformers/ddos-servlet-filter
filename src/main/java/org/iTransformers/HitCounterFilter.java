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
    private QuarantineController quarantineController;

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("\nFilter Initialization!!!");

        int blockingPeriod = Integer.parseInt(filterConfig.getInitParameter("BlockingPeriod"));

        String triggerIPAddress = filterConfig.getInitParameter("TriggerIPAddress");
        String triggerProtocol = filterConfig.getInitParameter("TriggerProtocol");
        String userName = filterConfig.getInitParameter("UserName");
        String userPass = filterConfig.getInitParameter("UserPass");
        String enablePass = filterConfig.getInitParameter("EnablePass");
        int triggerPort = Integer.parseInt(filterConfig.getInitParameter("TriggerPort"));
        int sshTimeout = Integer.parseInt(filterConfig.getInitParameter("sshTimeout"));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("protocol", triggerProtocol);
        params.put("username", userName);
        params.put("password", userPass);
        params.put("enable-password", enablePass);
        params.put("address", triggerIPAddress);
        params.put("port", triggerPort);
        params.put("timeout", sshTimeout);

        //params.put("blockingPeriod",blockingPeriod);
//        Initialize prefixCounter
        HashMap<String, PrefixCounter> prefixes = new HashMap<String, PrefixCounter>();

        filterConfig.getServletContext().setAttribute("prefixCounter", prefixes);
      //  filterConfig.getServletContext().setAttribute("params, params);

        quarantineController = new QuarantineController(prefixes, blockingPeriod, params);
        quarantineController.start();
    }

    public void destroy() {
        quarantineController.stop();
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


        HashMap<String, PrefixCounter> prefixes = (HashMap<String, PrefixCounter>) filterConfig.getServletContext().getAttribute("prefixCounter");

        CIDRUtils utils = new CIDRUtils(remoteAddr + "/" + triggerPrefixLength);

        final String networkAddress = utils.getNetworkAddress();
        final InetAddress iPv4LocalNetMask =  utils.getIPv4LocalNetMask();
        final String subnetMask = iPv4LocalNetMask.getHostAddress();

        String prefix = remoteAddr + "/" + triggerPrefixLength;

        if (!prefixes.containsKey(prefix) ){
             prefixes.put(prefix, new PrefixCounter(deltaPeriod, prefixHitCount, blockingPeriod));
        }

            final PrefixCounter prefixCounter = prefixes.get(prefix);
        if (prefixCounter.isPulled()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.reset();
        }
        //Prefix counter will pull the trigger if the trigger for that prefix is not already pulled!
        //If trigger is pulled but we still get a request we will reset it and will just encounter one more hit.
        //This will change the time in which the prefix will exit from quarantine!
        prefixCounter.hit(new StateChangedListener() {
            @Override
            public void stateChanged(boolean isQuarantine) {

                String triggerIPAddress = filterConfig.getInitParameter("TriggerIPAddress");
                String triggerProtocol = filterConfig.getInitParameter("TriggerProtocol");
                String userName = filterConfig.getInitParameter("UserName");
                String userPass = filterConfig.getInitParameter("UserPass");
                String enablePass = filterConfig.getInitParameter("EnablePass");
                int triggerPort = Integer.parseInt(filterConfig.getInitParameter("TriggerPort"));
                int sshTimeout = Integer.parseInt(filterConfig.getInitParameter("sshTimeout"));

                final Map<String, Object> params = new HashMap<String, Object>();
                params.put("protocol", triggerProtocol);
                params.put("username", userName);
                params.put("password", userPass);
                params.put("enable-password", enablePass);
                params.put("address", triggerIPAddress);
                params.put("port", triggerPort);
                params.put("timeout", sshTimeout);

                    System.out.println("Pulling the trigger for "+networkAddress+" with mask "+subnetMask);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Trigger.pullTrigger(networkAddress,subnetMask,"Null0","666",params);
                            } catch (Exception e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            prefixCounter.setPulled(true);
                        }
                    }).start();
            }

        });

        writer.println(prefix + "->" + prefixes.get(prefix));
        writer.flush();
        filterConfig.getServletContext().log(sw.getBuffer().toString());
        chain.doFilter(request, response);
    }
}