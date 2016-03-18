/*
 * HelloServlet.java
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
        ServletOutputStream os = resp.getOutputStream();
        printPage(os);
    }

    void printPage(ServletOutputStream os){
        try {
            os.print("<html>\n" +
                    "<body style=\"background-color:lightblue\"> " +
                    "<h1> DDOS, RTBH & Self-Protection training this September! </h1>" +
                    "    <b>When:</b>       30 September, 2014\n" +
                    "<br><b>Where:</b>      ESI CEE e-Competence center, Mladost 4, Business Park Sofia, bldg.11-B, fl.1\n" +
                    "<br><u><b>Last call:</b>    Thursday, 25 September, 2014</u>\n" +
                    "<br>\n" +
                    "<br>This is a devOps 1-day training aiming to introduce the participants to DDOS and one of the most popular methods for DDOS protection - RTBH. The course is half-day theory and half-day laboratory exercises. \n" +
                    "<br>The lab aims to teach you how to configure RTBH and also how to add some self-protection to your applications.\n" +
                    "<br>\n" +
                    "<h1><br><b>Agenda</b>\n" +
                    "<h2><br>DDOS and DDOS attach methods </h2> \n" +
                    "-\tDDOS definition\n" +
                    "<br>-\tDDOS attacks\n" +
                    "<br>-\tDDOS defense \n" +
                    "<h2>Network/Content Service Provider infrastructure</h2> \n" +
                    "-\tGeneral Concepts \n" +
                    "<br>-\tInternal Routing \n" +
                    "<br>-\tInternet Routing\n" +
                    "<h2><br>RTBH - Remotely triggered black hole </h2> \n" +
                    "<br>-\tSource based\n" +
                    "<br>-\tDestination based\n" +
                    "<h2>Self-adaptive systems </h2>\n" +
                    "-\tSelf *= Self-configurable, self-optimization, self-protected\n" +
                    "<br>-\tWhat is self-protection? \n" +
                    "<h2>Pre-requisites: </h2>Good knowledge of TCP/IP stack, some knowledge of network protocols, minimal set of programming skills. \n" +
                    "<br>Each participant should bring in his own laptop with virtual box installed.\n" +
                    "<br>\n" +
                    "<h2>Instructor: </h2></b> Nikolay Milovanov, engineer with 10 years of experience in networking &amp; software, 5 years experience as a Networks Solution Architect, currently Director PS and Support, specialized in Software Architecture in Carnegie Mellon University, PhD in IPv4 to IPv6 Network transformation. \n" +
                    "<h2>Certificate: </h2></b> Upon successful completion of the course attendees will receive a certificate from ESI CEE.\n"+
                     "</body>"+
                     "</html>");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
