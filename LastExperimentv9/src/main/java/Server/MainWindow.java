package Server;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainWindow extends JFrame {

    // Local Variables

    JTextField ipInput;

    // IP address
    InetAddress ipAddress = null;

    String backgroundColor = "#483D8B";
    String buttonColor = "#9370DB";
    String fontColor = "#FFFFFF";

    private void startClient(String name)
    {
        new Client(name);
    }

    public MainWindow()
    {
        // Creating window

        setBounds(1100,400,400,100);
        setTitle("Server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(Color.decode(backgroundColor));
        setResizable(false);

        //Container mainContainer = this.getContentPane();
        JPanel mainContainer = new JPanel();
//        mainContainer.setLayout(new GridLayout(2,0));
        mainContainer.setBackground(Color.decode(backgroundColor));

        JLabel emptyPlace = new JLabel("");
        JLabel emptyPlace1 = new JLabel("");
        JLabel emptyPlace2 = new JLabel("");
        JLabel emptyPlace3 = new JLabel("");
        JLabel emptyPlace4 = new JLabel("");
        JLabel emptyPlace5 = new JLabel("");
        JLabel emptyPlace6 = new JLabel("");
        JLabel emptyPlace7 = new JLabel("");

        JLabel mainLabel = new JLabel("Main Window");
        mainLabel.setFont(mainLabel.getFont().deriveFont(15f));
        mainLabel.setForeground(Color.decode(fontColor));


        JButton hostButton = new JButton("Host");
        hostButton.setBackground(Color.decode(buttonColor));
        hostButton.setForeground(Color.decode(fontColor));
        hostButton.setMargin(new Insets(10,20,10, 20));

        JButton joinButton = new JButton("Join");
        joinButton.setForeground(Color.decode(fontColor));
        joinButton.setBackground(Color.decode(buttonColor));
        joinButton.setMargin(new Insets(10,20,10, 20));


        mainContainer.add(emptyPlace);
        mainContainer.add(mainLabel);
        mainContainer.add(emptyPlace1);
        mainContainer.add(hostButton);
        mainContainer.add(emptyPlace2);
        mainContainer.add(joinButton);


        add(mainContainer, BorderLayout.CENTER);


        // Adding listeners

        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBounds(1100,300,400,100);
                hostButton.setVisible(false);
                joinButton.setVisible(false);
                Runnable task = () ->
                {
                    Server server = new Server();;
                };

                Thread thread2 = new Thread(task);
                thread2.start();
                try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                    datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
                    ipAddress = InetAddress.getByName(datagramSocket.getLocalAddress().getHostAddress());
                    mainLabel.setText("Server Created: Share IP with others \\(￣︶￣*\\))");
                }catch (Exception exception)
                {
                    System.out.println("No internet connection");
                    mainLabel.setText("No Internet");
                }

                String ipAddressString = String.valueOf(ipAddress);
                ipAddressString = ipAddressString.substring(1);


                JLabel ipAddressLabel = new JLabel(ipAddressString);
                ipAddressLabel.setFont(mainLabel.getFont().deriveFont(15f));
                ipAddressLabel.setForeground(Color.CYAN);
                JLabel textIp = new JLabel("Ip Address  : " );
                textIp.setForeground(Color.decode(fontColor));


                mainContainer.add(textIp);
                mainContainer.add(ipAddressLabel);
                startClient(ipAddressString);
            }
        });
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBounds(1100,400,400,200);
                mainContainer.setVisible(false);
                JPanel joinPanel = new JPanel();
                joinPanel.setLayout(new GridLayout(3,3,10,10));
                joinPanel.setBackground(Color.decode(backgroundColor));
                JPanel joinPanelTop = new JPanel();
                joinPanelTop.setLayout(new GridLayout(1,3,10,10));
                joinPanelTop.setBackground(Color.decode(backgroundColor));

                JLabel clientWindow = new JLabel("Client Window");
                clientWindow.setForeground(Color.decode(fontColor));
                clientWindow.setFont(mainLabel.getFont().deriveFont(15f));

                JLabel enterIP = new JLabel("Enter ip : ");
                enterIP.setForeground(Color.decode(fontColor));
                enterIP.setFont(enterIP.getFont().deriveFont(15f));
                ipInput = new JTextField();
                ipInput.setBackground(Color.decode(backgroundColor));
                ipInput.setForeground(Color.decode(fontColor));

                JButton connectButton = new JButton("Connect");
                connectButton.setMargin(new Insets(10,20,10, 20));
                connectButton.setForeground(Color.decode(fontColor));
                connectButton.setBackground(Color.decode(buttonColor));


                joinPanelTop.add(emptyPlace);
                joinPanelTop.add(clientWindow);
                joinPanelTop.add(emptyPlace1);
                joinPanel.add(emptyPlace2);
                joinPanel.add(emptyPlace3);
                joinPanel.add(emptyPlace4);
                joinPanel.add(enterIP);
                joinPanel.add(ipInput);
                joinPanel.add(emptyPlace5);
                joinPanel.add(connectButton);
                joinPanel.add(emptyPlace7);


                add(joinPanelTop,BorderLayout.NORTH);

                add(joinPanel, BorderLayout.CENTER);
                connectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!ipInput.getText().equals(""))
                        {
                            try{
                                emptyPlace5.setText("Connected");
                                emptyPlace5.setForeground(Color.GREEN);
                                startClient(ipInput.getText());
                            }catch (Exception exception)
                            {
                                emptyPlace5.setText("Try again T_T");
                            }
                        }
                        else {
                            emptyPlace5.setText("Try again. . .");
                        }


                    }
                });

            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });
        setVisible(true);
    }
}