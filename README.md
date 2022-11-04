# UDP_Ping

**To compile the server do: <br/>**
 `$ javac PingServer.java`  


**To run the server do: <br/>**

`$ java PingServer <port number>`   // Use registered ports i.e. b/w 1024 and 49151.
  

_ex : java PingServer 1026_
  


**If you get class not found error do: <br/>**

`$ java -classpath . PingServer <port number>`
  
<br/>
<br/>
<br/>

**To compile the Client do: <br/>**

`$ javac PingClient.java`
  

**To run the Client Pinger do: <br/>**

`$ java PingClient <Host Name> <port number>`    // Port number must be same as the server port number <br/>

_ex : java PingClient 127.0.0.1 1026_

 <br/>

**If you get class not found error do: <br/>**

`java -classpath . PingClient <Host Name> <port number>`

<br/>


**Remaining files can be done in the same way as above. <br/>**

  
**Note : UDP Server must be running to get a response, so run both Server and Client simultaneously.**
