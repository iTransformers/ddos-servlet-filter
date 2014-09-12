package org.iTransformers;

//import groovy.util.ResourceException;
//import groovy.util.ScriptException;
//import net.itransformers.expect4groovy.Expect4GroovyScriptLauncher;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/12/14
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Trigger {



    public static void pullTrigger(String subnet, String subnetMask,String gateway, String tag) throws Exception {
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("protocol", "telnet");
//        params.put("username", "lab");
//        params.put("password", "lab123");
//        params.put("enable-password", "lab123");
//        params.put("address", "193.19.172.133");
//        params.put("port", 11123);
//
//        Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();
//
//        Map<String, Object> loginResult = launcher.open(new String[]{"." + File.separator}, "cisco_login.groovy", params);
//
//
//        if (loginResult.get("status").equals(2)) {
//            System.out.println(loginResult);
//        } else {
//            Map<String, Object> cmdParams = new LinkedHashMap<String, Object>();
//            cmdParams.put("evalScript", null);
//            cmdParams.put("command",String.format("ip route %s %s %s tag %s",subnetMask,gateway,tag));
//            Map<String, Object> result = launcher.sendCommand("cisco_sendConfigCommand.groovy",cmdParams);
//            params.put("configMode", result.get("configMode"));
//            cmdParams.put("command","ip route 10.200.1.0 255.255.255.0 192.0.2.1");
//            launcher.sendCommand("cisco_sendConfigCommand.groovy", cmdParams);
//            launcher.close("cisco_logout.groovy");
//        }
    }
}
