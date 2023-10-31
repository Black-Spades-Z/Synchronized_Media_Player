package Server;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class Server {
    // порт, который будет прослушивать наш сервер
    static final int PORT = 3443;
    // IP Address
    InetAddress ipAddress = null;



    // список клиентов, которые будут подключаться к серверу
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server() {
        // сокет клиента, это некий поток, который будет подключаться к серверу
        // по адресу и порту
        Socket clientSocket = null;

        // серверный сокет
        ServerSocket serverSocket = null;

        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            ipAddress = InetAddress.getByName(datagramSocket.getLocalAddress().getHostAddress());
        }catch (Exception e)
        {
            System.out.println("No internet connection");
        }
        try {
            // создаём серверный сокет на определенном порту
            serverSocket = new ServerSocket(PORT,2, ipAddress);
            System.out.println("Starting the Server");
            System.out.println("IP Address : " + String.valueOf(ipAddress));
            // запускаем бесконечный цикл
            while (true) {
                // таким образом ждём подключений от сервера
                clientSocket = serverSocket.accept();
                // создаём обработчик клиента, который подключился к серверу
                // this - это наш сервер
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                // каждое подключение клиента обрабатываем в новом потоке
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                // закрываем подключение
                clientSocket.close();
                System.out.println("Stopping the server");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }


    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}
