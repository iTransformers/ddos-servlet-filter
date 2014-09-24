package org.iTransformers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //PrefixCounter counter =  (PrefixCounter)app.getAttribute("hitCounter");
        ServletOutputStream os = resp.getOutputStream();
        String remoteAddr = req.getRemoteAddr();

//        HashMap<String, PrefixCounter2> prefixes = (HashMap<String, PrefixCounter2>) app.getAttribute("prefixCounter");
        os.println("remote addr: " + remoteAddr);
//        os.println("Prefixes="+prefixes+"<br>");

    }
}
