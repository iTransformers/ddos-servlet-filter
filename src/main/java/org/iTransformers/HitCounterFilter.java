package org.iTransformers;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class HitCounterFilter implements Filter {
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("\nFilter Initialization!!!");

        //Dump filter initialization parameters!!!

        System.out.println("\nTriggerPrefixLength: " + filterConfig.getInitParameter("TriggerPrefixLength"));
        System.out.println("\nPrefixHitCount: " + filterConfig.getInitParameter("PrefixHitCount"));

        //Initialize prefixCounter
        Prefixes prefixes = new Prefixes();
        Thread filterThread;

        filterConfig.getServletContext().setAttribute("prefixCounter", prefixes);
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

        CIDRUtils cidrUtils = new CIDRUtils(remoteAddr + "/" + triggerPrefixLength);
        String networkAddress = cidrUtils.getNetworkAddress();
        String prefix = remoteAddr + "/" + triggerPrefixLength;


        if (prefixes.getPrefix(prefix) != null) {
            PrefixCounter prefixCounter = prefixes.getPrefix(prefix);

            if (!prefixes.getPrefix(prefix).isQuarantined()) {
                int counter = prefixCounter.getHitCounter();
                if (counter >= prefixHitCount) {
                    System.out.println("Pull the trigger!!!");
                    try {
                        Trigger.pullTrigger(networkAddress,triggerPrefixLength,"Null0","666");
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
                System.out.println("Quarantine End!");
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