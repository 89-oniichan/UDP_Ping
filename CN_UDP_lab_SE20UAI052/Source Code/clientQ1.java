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
