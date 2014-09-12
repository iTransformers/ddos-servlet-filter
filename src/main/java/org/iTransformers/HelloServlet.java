package org.iTransformers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/1/14
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class HelloServlet extends HttpServlet
{
    private ServletContext app;

    @Override
    public void init(ServletConfig config) throws ServletException {
        app = config.getServletContext();
        System.out.println(config.getInitParameter("test"));
        Map<String, String> map = new HashMap<String, String>();
        app.setAttribute("vasko",map);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map map = (Map) app.getAttribute("vasko");
        PrefixCounter counter =  (PrefixCounter)app.getAttribute("hitCounter");
        ServletOutputStream os = resp.getOutputStream();
        String remoteAddr = req.getRemoteAddr();

        Prefixes prefixes = (Prefixes) app.getAttribute("prefixCounter");
        os.println("remote addr: " + remoteAddr);
        os.println("Prefixes="+prefixes+"<br>");

    }
}
