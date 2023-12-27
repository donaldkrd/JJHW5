package chat.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
//        System.out.println("Run chat client.");
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите своё имя:");
        String name = scan.nextLine();
        /**
         * Создаем новый объект сокета.
         * Передаем ip адрес нашего сервера, в данном случае это localhost
         * и порт, к которому мы хотим подключится
         */
        try {
            Socket socket = new Socket("localhost", 1400);
            Client client = new Client(socket, name);

            InetAddress address = socket.getInetAddress();
            System.out.println("InetAddress: " + address);
            String remotIp = address.getHostAddress();
            System.out.println("Remote IP: " + remotIp);
            System.out.println("LocalPort: " + socket.getLocalPort());

            client.listenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * Можно получить локальный ip адрес машины следующим способом:
         */
//        try {
//            InetAddress address = InetAddress.getLocalHost();
//            System.out.println(address); // Anton/192.168.43.30
//            System.out.println(InetAddress.getLocalHost().getHostAddress()); // 192.168.43.30
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
    }
}
