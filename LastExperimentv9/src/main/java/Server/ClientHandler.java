package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static com.example.lastexperiment.HelloApplication.clientCommandList;

// Implementing Runnable to use threads
public class ClientHandler implements Runnable {
    // our server
    private Server server;
    // sending message
    private PrintWriter outMessage;
    // Out boolean
    private DataOutputStream outBoolean;
    // receiving message
    private Scanner inMessage;
    // Receiving boolean
    private DataInputStream inBoolean;

    private static final String HOST = "172.18.2.173";
    private static final int PORT = 3443;
    // Client socket
    private Socket clientSocket = null;
    // Number of Viewers in Server, static area
    private static int clients_count = 0;

    // Constructor which accepts params
    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // Overwriting Run to start when new Thread is created

    @Override
    public void run() {
        try {
            while (true) {
                // server sends a message
                server.sendMessageToAllClients("New member in the Server!");
                server.sendMessageToAllClients("Viewers in Server = " + clients_count);
                break;
            }

            while (true) {
                // If there is message from viewer
                if (inMessage.hasNext()) {

                    String clientMessage = inMessage.nextLine();
                    // if viewer sends this message, he leaves the Server
                    // client is leaving Server
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    if(clientMessage.equals("##session##stop##"))
                    {
                        clientCommandList.add(0,clientMessage);
                        // Printing to console
                        System.out.println(clientMessage);
                        // sending this message to all viewers
                        server.sendMessageToAllClients(clientMessage);
                    }
                    else if(clientMessage.equals("##session##play##"))
                    {
                        clientCommandList.add(0,clientMessage);
                        // Printing to console
                        System.out.println(clientMessage);
                        // sending this message to all viewers
                        server.sendMessageToAllClients(clientMessage);
                    }

                    else if(clientMessage.equals("##session##pause##"))
                    {
                        clientCommandList.add(0,clientMessage);
                        // Printing to console
                        System.out.println(clientMessage);
                        // sending this message to all viewers
                        server.sendMessageToAllClients(clientMessage);
                    }

                    // Printing to console
                    System.out.println(clientMessage);
                    // sending this message to all viewers
                    server.sendMessageToAllClients(clientMessage);
                }
                // stopping the thread for 0.1 sec
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
    // sending a message
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // client leaves us
    public void close() {
        // deleting him from the list
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Viewers in Server = " + clients_count);
    }
}