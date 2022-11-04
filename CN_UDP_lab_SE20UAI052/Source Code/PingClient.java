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
