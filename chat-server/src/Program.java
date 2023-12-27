package chat.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Program {
    public static void main(String[] args) {
//        System.out.println("Run chat server.");
        /**
         * Создаем объект, который будет слушать порт сокета.
         * Указываем на каком порту наш сервер будет слушать сообщения
         */
        try {
            ServerSocket serverSocket = new ServerSocket(1400);
            Server server = new Server(serverSocket);
            server.runServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}