package Server;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.lastexperiment.HelloApplication.clientCommandList;


public class Client extends JFrame {

    public static final ArrayList<Socket> clientSocketList = new ArrayList<>();

    // port
    private static final int SERVER_PORT = 3443;
    // client socket
    private Socket clientSocket;
    // receiving message
    private Scanner inMessage;
    // sending message
    private PrintWriter outMessage;
    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    // name of client
    private String clientName = "";
    // Getting Client name
    public String getClientName() {
        return this.clientName;
    }

    // constructor
    public Client(String SERVER_HOST) {
        try {
            // Connecting to the server
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Setting up display
        setBounds(0, 0, 400, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        // label, which shows number of viewers in the server
        JLabel jlNumberOfClients = new JLabel("Viewers in Server = ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Send");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Enter your message: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Enter your name: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);
        // setting up ActionListener to send button
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if name of viewer and message is not empty sending the message
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    // focusing to message area
                    jtfMessage.grabFocus();
                }
            }
        });

        // Adding socket to SocketList

        clientSocketList.add(clientSocket);

        // if there is focus area is being cleaned
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        // if there is focus area is being cleaned
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });
        // in another thread starting working with server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Infinite loop
                    while (true) {
                        // if there is message
                        if (inMessage.hasNext()) {
                            // reading it
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Viewers in Server = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {

                                if(inMes.equals("##session##stop##"))
                                {
                                    clientCommandList.add(0,inMes);
                                    // Printing to console
                                }
                                else if(inMes.equals("##session##play##"))
                                {
                                    clientCommandList.add(0,inMes);
                                }
                                else if(inMes.equals("##session##pause##"))
                                {
                                    clientCommandList.add(0,inMes);
                                }
                                else{
                                    // displaying it
                                    jtaTextAreaMessage.append(inMes);
                                    // adding row
                                    jtaTextAreaMessage.append("\n");
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        // creating windowing closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // checking whether viewer has a nickname
                    if (!clientName.isEmpty() && clientName != "Enter your nickname: ") {
                        outMessage.println(clientName + " left the server T_T");
                    } else {
                        outMessage.println("Anonymous Viewer left the server T_T");
                    }
                    // sending message about viewer who left
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
        setVisible(true);

    }
    public void sendMsg() {
        // forming message to send it to server
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        // sending message
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }




}