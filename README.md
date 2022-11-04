# UDP_Ping

To compile the server do 
javac PingServer.java

To run the server do 
java PingServer <port number>   // Use registered ports i.e. b/w 1024 and 49151.
ex : java PingServer 1026
  
If you get class not found error do
java -classpath . PingServer <port number>
  
  

To compile the Client do 
javac PingClient.java

To run the Client Pinger do 
java PingClient <Host Name> <port number>    // Port number must be same as the server port number
ex : java PingClient 127.0.0.1 1026
  
If you get class not found error do
java -classpath . PingClient <Host Name> <port number>


Remaining files can be done in the same way as above.
  
Note : UDP Server must be running to get a response, so run both Server and Client simultaneously.
