/*
 * Trigger.java
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

import groovy.lang.Binding;
import groovy.lang.Script;
import net.itransformers.expect4groovy.Expect4Groovy;
import net.itransformers.expect4java.cliconnection.CLIConnection;
import net.itransformers.expect4java.cliconnection.impl.EchoCLIConnection;
import net.itransformers.expect4java.cliconnection.impl.RawSocketCLIConnection;
import net.itransformers.expect4java.cliconnection.impl.SshCLIConnection;
import net.itransformers.expect4java.cliconnection.impl.TelnetCLIConnection;
import net.itransformers.scripts.cisco_login;
import net.itransformers.scripts.cisco_logout;
import net.itransformers.scripts.cisco_sendConfigCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/12/14
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Trigger implements QuarantineControllerAction {


    public static void pullTrigger(String subnet, String subnetMask,String gateway, String tag,Map<String, Object> params) throws Exception {

//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("protocol", "telnet");
//        params.put("username", "lab");
//        params.put("password", "lab123");
//        params.put("enable-password", "lab123");
//        params.put("address", "193.19.172.133");
//        params.put("port", 2);

        CLIConnection conn = createCliConnection((String) params.get("protocol"));
        conn.connect(params);
        Binding binding = new Binding();
        Expect4Groovy.createBindings(conn, binding, true);
        binding.setProperty("params", params);

        Map<String, Object> loginResult = executeScript(cisco_login.class, binding);

        if (loginResult.get("status").equals(2)) {
            System.out.println(loginResult);
            return;
        }

        params.put("evalScript", null);
        params.put("command",String.format("ip route %s %s %s tag %s",subnet, subnetMask,gateway,tag));

        Map<String, Object> result = executeScript(cisco_sendConfigCommand.class, binding);
        params.put("configMode", result.get("configMode"));
        params.put("command",String.format("ip route %s %s %s", subnet, subnetMask, gateway));

        executeScript(cisco_sendConfigCommand.class, binding);

        executeScript(cisco_logout.class, binding);

    }
    public static void pullOffTrigger(String subnet, String subnetMask,String gateway, String tag, Map<String, Object> params) throws Exception {


//        Map<String, Object> params = new HashMap<String, Object>();
//    params.put("protocol", "telnet");
//    params.put("username", "lab");
//    params.put("password", "lab123");
//    params.put("enable-password", "lab123");
//    params.put("address", "193.19.172.133");
//    params.put("port", 11123);

    CLIConnection conn = createCliConnection((String) params.get("protocol"));
    conn.connect(params);
    Binding binding = new Binding();
    Expect4Groovy.createBindings(conn, binding, true);
    binding.setProperty("params", params);

    Map<String, Object> loginResult = executeScript(cisco_login.class, binding);

    if (loginResult.get("status").equals(2)) {
        System.out.println(loginResult);
        return;
    }

    params.put("evalScript", null);
   // params.put("command",String.format("ip route %s %s %s tag %s",subnet, subnetMask,gateway,tag));

  //  Map<String, Object> result = executeScript(cisco_sendConfigCommand.class, binding);
  //  params.put("configMode", result.get("configMode"));
    params.put("command",String.format("no ip route %s %s %s", subnet, subnetMask, gateway));

    executeScript(cisco_sendConfigCommand.class, binding);

    executeScript(cisco_logout.class, binding);

}

    private static Map<String, Object> executeScript(Class clazz, Binding binding) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Script> scriptConstructor = clazz.getConstructor(Binding.class);
        Script script = scriptConstructor.newInstance(binding);
        return (Map<String, Object>) script.run();
    }

    private static CLIConnection createCliConnection(String protocol) {
        CLIConnection conn;
        if ("telnet".equals(protocol)) {
            conn = new TelnetCLIConnection();
        } else if ("raw".equals(protocol)) {
            conn = new RawSocketCLIConnection();
        } else if ("echo".equals(protocol)) {
            conn = new EchoCLIConnection();
        } else {
            conn = new SshCLIConnection();
        }
        return conn;
    }

//    public static void main(String[] args) throws Exception {
//        pullTrigger("10.200.1.0", "255.255.255.0", "192.0.2.1", "");
//        pullOffTrigger("10.200.1.0", "255.255.255.0", "192.0.2.1", "");
//
//    }

    @Override
    public void enterQuarantine(String prefix, Map<String, Object> params) {
        CIDRUtils utils;
        try {
            utils = new CIDRUtils(prefix);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        final String networkAddress = utils.getNetworkAddress();
        final InetAddress iPv4LocalNetMask =  utils.getIPv4LocalNetMask();
        final String subnetMask = iPv4LocalNetMask.getHostAddress();

        System.out.println("Pulling the trigger for " + networkAddress + " with mask " + subnetMask);
        try {
            Trigger.pullTrigger(networkAddress, subnetMask, "Null0", "666", params);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void exitQuarantine(String prefix, Map<String, Object> params) {
        System.out.println("Prefix "+ prefix +" quarantine has finished. So let's pull off the trigger!!!");
        try {
            CIDRUtils utils = new CIDRUtils(prefix);
            Trigger.pullOffTrigger(utils.getNetworkAddress(), utils.getIPv4LocalNetMask().getHostAddress(), "Null0", "666", params);
            // TODO handle exceptions in a better way!!!
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}
