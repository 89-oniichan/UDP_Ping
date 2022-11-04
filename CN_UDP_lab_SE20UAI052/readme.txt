# Server Code 

import java.io.*;
import java.net.*;
import java.util.*;

/*
* Server to process ping requests over UDP.
*/
public class PingServer {
    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 100; // milliseconds

    public static void main(String[] args) throws Exception {
        
        // Get command line argument.
        if (args.length != 1) {
            System.out.println("Required arguments: port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        
        // Create random number generator for use in simulating
        // packet loss and network delay.
        Random random = new Random();

        // Create a datagram socket for receiving and sending UDP packets
        // through the port specified on the command line.
        DatagramSocket socket = new DatagramSocket(port);

        // Processing loop.
        while (true) {
            
            // Create a datagram packet to hold incomming UDP packet.
            DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
            
            // Block until the host receives a UDP packet.
            socket.receive(request);

            // Print the recieved data.
            printData(request);
            
            // Decide whether to reply, or simulate packet loss.
            if (random.nextDouble() < LOSS_RATE) {
                System.out.println(" Reply not sent.");
                continue;
            }

            // Simulate network delay.
            Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
            
            // Send reply.
            InetAddress clientHost = request.getAddress(); // client hostname
            int clientPort = request.getPort();  // client port
            byte[] buf = request.getData();  // content
            DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
            socket.send(reply);
            System.out.println(" Reply sent.");
        }
    }

    /*
    * Print ping data to the standard output stream.
    */
    private static void printData(DatagramPacket request) throws Exception 
{
        
        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();
        
        // Wrap the bytes in a byte array input stream,
        // so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);
        
        // Wrap the input stream reader in a bufferred reader,
        // so you can read the character data a line at a time.
        // (A line is a sequence of chars terminated by any combination of \r and \n.)
        BufferedReader br = new BufferedReader(isr);
        
        // The message data is contained in a single line, so read thisline.
        String line = br.readLine();
        
        // Print host address and data received from it.
        System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
    }
}












# Client Code


import java.io.*;
import java.net.*;
import java.util.*;
//SE20UARI052

public class PingClient 
{
    private static final int tout = 1000; // milliseconds time out
    private static final int maxpingreq = 10; // number of ping requests 
    private static final int clientport = 5000; // client port 
    private static InetAddress servhost = null;
    private static int serverPort = 0;
    private static DatagramSocket socket = null;

    public static void main(String[] args) throws Exception 
    {

        if (args.length != 2) 
        {
            System.out.println("Required arguments: host port");
            return;
        }

        // Getting host and port
        servhost = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);

        // Datagram socket for receiving and sending UDP packets

        // through the port specified on the command line.
        socket = new DatagramSocket(clientport);
        socket.setSoTimeout(tout); // sets time out so that command line quit waiting endless

        int seqNumb = -1;
        while (++seqNumb < maxpingreq) 
        {
            // Create Server Response Datagram
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

            // Create Timer
            Date date = new Date();
            long timestamp = date.getTime();

            // Create message that will be sent to the server
            String sendMessage = "PING " + seqNumb + " " + timestamp + " \r\n";

            // Convert message to byte array
            byte[] buffer = new byte[1024];
            buffer = sendMessage.getBytes();

            // Send datagram to server
            DatagramPacket pingRequest = new DatagramPacket(buffer, buffer.length, servhost, serverPort);
            socket.send(pingRequest);

            // Receive response from server
            try {
                // Try to receive the packet from the server
                socket.receive(response);

                // Calculate response time
                date = new Date();
                long delayReceived = date.getTime() - timestamp;

                System.out.print("Delay " + delayReceived + " ms: ");
                printData(response);
            }
            catch (SocketTimeoutException e) 
            {
                System.out.println("Lost Package : " + sendMessage);
            }
        }
    }

    /*
    * Print ping data to the standard output stream.
    */
    private static void printData(DatagramPacket request) throws Exception 
{

        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();

        // Wrap the bytes in a byte array input stream,
        // so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);

        // Wrap the input stream reader in a bufferred reader,
        // so you can read the character data a line at a time.
        // (A line is a sequence of chars terminated by any combination of \r and \n.)
        BufferedReader br = new BufferedReader(isr);

        // The message data is contained in a single line, so read this line.
        String line = br.readLine();

        // Print host address and data received from it.
        System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
    }
}









# Question 1 code 




import java.io.*;
import java.net.*;
import java.util.*;

public class clientQ1 {
    private static final int tout = 1000; // milliseconds time out
    private static final int maxpingreq = 10; // number of ping requests
    private static final int clientport = 5000; // client port
    private static InetAddress servhost = null;
    private static int serverPort = 0;
    private static DatagramSocket socket = null;

    public static void main(String[] args) throws Exception 
    {

        if (args.length != 2) 
        {
            System.out.println("Required arguments: host port");
            return;
        }
        // Recover host and port
        servhost = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);

        // Create a datagram socket for receiving and sending UDP packets
        // through the port specified on the command line.
        socket = new DatagramSocket(clientport);
        socket.setSoTimeout(tout);

        int sequence_number = -1;

        // Array to store delay values
        Long[] delay = new Long[maxpingreq];

        while (++sequence_number < maxpingreq) 
        {
            // Create Server Response Datagram
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

            // Create Timer
            Date date = new Date();
            long timestamp = date.getTime();

            // Create message that will be sent to the server
            String sendMessage = "PING " + sequence_number + " " + timestamp + " \r\n";

            // Convert msg to byte array
            byte[] buffer = new byte[1024];
            buffer = sendMessage.getBytes();

            // Send datagram to server
            DatagramPacket pingRequest = new DatagramPacket(buffer, buffer.length, servhost, serverPort);
            socket.send(pingRequest);

            // Receive response from server
            try {
                // Try to receive the packet from the server
                socket.receive(response);

                // Calculate response time
                date = new Date();
                long delayReceived = date.getTime() - timestamp;
                
                // Save to Delay array
                delay[sequence_number] = delayReceived;

                System.out.print("Delay " + delayReceived + " ms: ");
                printData(response);
            }
            catch (SocketTimeoutException e) 
            {
                System.out.print("Pacote perdido: " + sendMessage);
                delay[sequence_number] = Long.valueOf(tout);
            }
        }

        // Calculate RTT
        roundTripTime(delay);
        
    }

    /*
    * Print ping data to the standard output stream.
    */
    private static void printData(DatagramPacket request) throws Exception 
{

        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();

        // Wrap the bytes in a byte array input stream,
        // so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);

        // Wrap the input stream reader in a bufferred reader,
        // so you can read the character data a line at a time.
        // (A line is a sequence of chars terminated by any combination of \r and \n.)
        BufferedReader br = new BufferedReader(isr);

        // The message data is contained in a single line, so read this line.
        String line = br.readLine();

        // Print host address and data received from it.
        System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
    }

    private static void roundTripTime(Long[] delay) 
    {

        long minDelay = delay[0];
        long maxDelay = delay[0]; 
        long averageDelay = 0;

        for (int i = 0; i < delay.length; i++) 
        {
            long d = delay[i];
            if (d < minDelay) 
            {
                minDelay = d;
            }

            if (d > maxDelay) 
            {
                maxDelay = d;
            }

            averageDelay += d;
        }

        averageDelay /= delay.length;

        System.out.println("RTT: minDelay: " + minDelay + "ms / maxDelay: " + maxDelay + "ms / averageDelay: " + averageDelay + "ms");

    }
}













# Question 2 code



import java.io.*;
import java.net.*;
import java.util.*;

public class clientQ2 {
    private static final int tout = 1000; // milliseconds time out
    private static final int maxpingreq = 10; // number of ping requests
    private static final int clientport = 5000; // client port
    private static InetAddress servhost = null;
    private static int serverPort = 0;
    private static DatagramSocket socket = null;

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Required arguments: host port");
            return;
        }

        // Recover host and port
        servhost = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);

        // Create a datagram socket for receiving and sending UDP packets
        // through the port specified on the command line.
        socket = new DatagramSocket(clientport);
        socket.setSoTimeout(tout);

        // Request ping every 1 second
        Timer timer = new Timer();
        RemindTask remindTask = new RemindTask(maxpingreq, socket, servhost, serverPort);
        timer.schedule(remindTask, 0, 1000);
    }
    
    /*
    * Request/Reply ping.
    */
    public static void ping(DatagramSocket socket, int sequence_number, InetAddress servhost, int serverPort) {
        // Create Server Response Datagram
        DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

        // Create Timer
        Date date = new Date();
        long timestamp = date.getTime();

        // Create message that will be sent to the server
        String sendMessage = "PING " + sequence_number + " " + timestamp + " \r\n";

        // Convert msg to byte array
        byte[] buffer = new byte[1024];
        buffer = sendMessage.getBytes();

        // Send datagram to server
        DatagramPacket pingRequest = new DatagramPacket(buffer, buffer.length, servhost, serverPort);

        try {
            socket.send(pingRequest);

            // Try to receive the packet from the server
            socket.receive(response);

            // Calculate response time
            date = new Date();
            long delayReceived = date.getTime() - timestamp;

            System.out.print("Delay " + delayReceived + " ms: ");
            printData(response);
        } catch (SocketTimeoutException e) {
            System.out.print("Pacote perdido: " + sendMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * Print ping data to the standard output stream.
    */
    private static void printData(DatagramPacket request) throws Exception 
{

        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();

        // Wrap the bytes in a byte array input stream,
        // so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);

        // Wrap the input stream reader in a bufferred reader,
        // so you can read the character data a line at a time.
        // (A line is a sequence of chars terminated by any combination of \r and \n.)
        BufferedReader br = new BufferedReader(isr);

        // The message data is contained in a single line, so read this line.
        String line = br.readLine();

        // Print host address and data received from it.
        System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
    }
}

class RemindTask extends TimerTask {
    private int maxPingRequests;
    private int times = -1;
    private DatagramSocket socket;
    private InetAddress servhost;
    private int serverPort;
    

    public RemindTask(int maxPingRequests, DatagramSocket socket, InetAddress servhost, int serverPort) {
        this.maxPingRequests = maxPingRequests;
        this.socket = socket;
        this.servhost = servhost;
        this.serverPort = serverPort;
    }

    public void run() {
        if (++this.times < this.maxPingRequests) {
         clientQ2.ping(this.socket, this.times, this.servhost, this.serverPort);
        } else {
            System.exit(0);
        }
    }
}




