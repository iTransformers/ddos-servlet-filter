DDOS Servlet Filter
=================

The time of static applications that just wait to be hit by a massive number of requests has gone. 

This piece of code is an example of a Self-Protective servlet filter able to defend itself against DDOS by utilizing the Remotely Triggered Black Holling technique. 

DDOS servlet filter is able to apply a remotely triggered black holing as per [RFC 5635](https://tools.ietf.org/html/rfc5635). 


The servlet filter will track the number of requests comming from certain prefix. 
![alt tag](http://itransformers.net/nets/ddos/images/Untitled.png)

If certain threashold is triggered will pull the RTBH trigger and will put it in quarantine. 

![alt tag](http://itransformers.net/nets/ddos/images/Untitled2.png)

Eventually the trigger route will be redistributed in the network and the attacker will be blocked for certain preconfigured period of time. 
![alt tag](http://itransformers.net/nets/ddos/images/Untitled3.png)


There is also a quarantineController initialized in the init method of the servlet filter that check for prefixes with expired quarantine period. For those the trigger route will be deleted. 

Note that in order that servlet filter to be useful for you you will have to have access to the infrastructure of your provider. This simply an example on how enterprise java applications could benefit from RTBH. 

![Community forum](http://forum.itransformers.net/fluxbb/viewforum.php?id=25) 

![Issue tracker](https://github.com/iTransformers/ddos-servlet-filter/issues)
