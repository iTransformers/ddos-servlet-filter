package org.iTransofrmers.scripts
println (System.currentTimeMillis())

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/23/14
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */

prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = false
hostname = ""
status = ["success": 1, "failure": 2]

exit()

def exit(){
    if (params["configMode"] == true) {
        send(""+(char)0x1A) // ^z
        expect(params["hostname"]+powerUserPrompt)
        send("exit"+defaultTerminator)
        expect _eof()
    }
    return ["status": 1, "data": "Logout Success!"]
}