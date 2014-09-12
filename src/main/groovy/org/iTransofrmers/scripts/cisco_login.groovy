package org.iTransofrmers.scripts
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

def result;

int loginStatus = login();
if (loginStatus == status["success"]) {
    int setupTerminalStatus = setupTerminal();
    if (setupTerminalStatus == status["success"]) {

        result = ["status": 1, "data": "Login Success!", "hostname": hostname]
    } else {
        result = ["status": 2, "data": "Login Failure!"]
    }
} else {
    result = ["status": 2, "data": "Login Failure!"]
}

return result;

def login() {
    if (params["protocol"] != "ssh") {
        if (user() == status["success"]) {
            if (password() == status["success"]) {
                if (showPrivilege() == status["success"]) {
                    println("Login Successfull")
                    return status["success"]
                } else {
                    if (enablePassword() == status["success"]) {
                        println("Login Successfull")
                        return status["success"]
                    } else {
                        return status["failure"]
                    }
                }

            } else {
                return status["failure"]

            }
        } else {
            return status["failure"]
        }
    } else {
        setupTerminal();
        if (showPrivilege() == status["success"]) {
            println("Login Successfull")
            return status["success"]
        } else {
            if (enablePassword() == status["success"]) {
                println("Login Successfull")
                return status["success"]
            } else {
                return status["failure"]
            }
        }
    }
}

def user() {
    def returnFlag = 2;

    expect([
            _re("Username:|User:") {
                send(params["username"] + defaultTerminator)
                returnFlag = status["success"]
            }
    ])
    if (returnFlag != status["success"]) {
        println "Login failed!!!"
        return status["failure"]
    } else {
        return status["success"]
    }

}

def password() {
    def returnFlag = 2;

    expect("Password:") {
        send(params["password"] + defaultTerminator)
        expect([
                _re("(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
                    //  println("Password submitted successfully")
                    returnFlag = status["success"]
                    //   println("Password submitted successfully: "+returnFlag)
                }
        ])
    }
    //  println("Password submitted successfully2: "+returnFlag)

    return returnFlag;
}

def showPrivilege() {
    def returnFlag = 2
                send("show privilege" + defaultTerminator)
                expect ([

                        _re("show privilege"+defaultTerminator) {
                            it.getBuffer();

                        }]);

                  expect([
                        _re("Current privilege level is (\\d+)") {
                            it.getMatch(1);
                            if (it.getMatch(1) == "15") {
                                returnFlag = status["success"]

                            } else {
                                returnFlag = status["failure"]

                            }
                        }
                        ]);



//    expect([
////            _re("([^\n]*)") {
////                it.getBuffer();
////            },
//            _
//    ])




    return returnFlag
}

def enablePassword() {
    def returnFlag = 2
    expect([
            _re("(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
                send("enable" + defaultTerminator)
                expect("Password:") {
                    send(params["enable-password"] + defaultTerminator)
                    expect _re(powerUserPrompt + "\$") {
                        logedInPowerMode = "true"
//                send "terminal length 0" + defaultTerminator
                        returnFlag = status["success"]
                    }
                }
            }
    ])
    return returnFlag

}

def setupTerminal() {
    def returnFlag = 2
    send("terminal length 0" + defaultTerminator)

    expect([
            _re("[\r][\n]((([^" + powerUserPrompt + "]+)" + powerUserPrompt + "\$)" + "|" + "(([^" + prompt + "]+)" + prompt + "\$" + "))") {
                def match1 = it.getMatch(3)
                def match2 = it.getMatch(5)

                if (match1 == null) {
                    params["hostname"] = match2
                    logedIn = "true"
                    returnFlag = status["success"]

                } else {
                    params["hostname"] = match1
                    logedInPowerMode = "true"
                    returnFlag = status["success"]
                }

            }
    ])
    println("Terminal length set to 0")
    return returnFlag
}