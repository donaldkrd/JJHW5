package chat.server;

import javax.crypto.spec.PSource;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс для хранения информации о клиентах
 */

public class ClientManager implements Runnable{ // Делаем для запуска отдельным потоком
    private String name;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    /**
     * Коллекцию делаем статической, чтобы избежать зацикливания и мы в будущем, при подключении
     * нового клиента, можем добавлять этого клиента в коллекцию, коллекция будет одна для всех клиентов.
     */
    public static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: " + name + " подключился к чату.");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClient();
        try {
            /**
             * Делаем для того, чтобы проверить, если что-то успело проинициализироваться, то это закрываем
             */
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }
    /**
     * Удаление клиента из коллекции
     */
    private void removeClient(){
        clients.remove(this);
        System.out.println(name + " покинул чат.");
    }

    @Override
    public void run() {
        /**
         * При вызове класса в отдельном потоке, автоматически сработает метод run()
         * который будет запускать цикл считывания данных от клиента
         */
        while (socket.isConnected()){
            String messageFromClient;
            try{
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException e){
                closeEverything(socket,bufferedWriter, bufferedReader);
                break;
            }
        }
    }
    /**
     * Отправка сообщений всем слушателям
     * ToDo: реализовать приватные сообщения
     */
    private void broadcastMessage(String message) {
        String[] messageArray = message.split(" ");
        boolean isPresent = false;
        if (messageArray[1].charAt(0) == '$') {
            StringBuilder privateMessage = new StringBuilder();
            String messageFor = messageArray[1].substring(1);
            for (int i = 2; i < messageArray.length; i++) {
                privateMessage.append(messageArray[i]).append(" ");
            }
            for (ClientManager client : clients) {
                try {
                    if (client.name.equals(messageFor)){
                        client.bufferedWriter.write(name + ": " + String.valueOf(privateMessage));
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                        isPresent = true;
                    }
                } catch (IOException e){
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
            if (!isPresent) {
                try {
                    for (ClientManager cl : clients) {
                        if (cl.name.equals(name)) {
                            cl.bufferedWriter.write("Клиента с таким именем нет в чате.");
                            cl.bufferedWriter.newLine();
                            cl.bufferedWriter.flush();
                        }
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
        } else {
            for (ClientManager client : clients) {
                try {
                    if (!client.name.equals(name)){
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                } catch (IOException e){
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
        }
    }
//    private void broadcastMessage(String message) {
//        for (ClientManager client : clients) {
//            try {
//                // Если клиент не равен по наименованию клиенту-отправителю,
//                // отправим сообщение
//                if (!client.name.equals(name)){
//                    client.bufferedWriter.write(message);
//                    client.bufferedWriter.newLine();
//                    client.bufferedWriter.flush();
//                }
//            } catch (IOException e){
//                closeEverything(socket, bufferedWriter, bufferedReader);
//            }
//        }
//    }

}
