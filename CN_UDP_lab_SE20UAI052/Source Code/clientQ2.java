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
