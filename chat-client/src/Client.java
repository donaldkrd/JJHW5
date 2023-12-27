package chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final String name;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e){
            closeEveryThing(socket, bufferedWriter, bufferedReader);
        }
    }

    /**
     * Метод отправки сообщений
     */
    public void sendMessage(){
        try {
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){ // По подключение активно
                String message = scanner.nextLine();
                bufferedWriter.write(name + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEveryThing(socket, bufferedWriter, bufferedReader);
        }
    }

    /**
     * Создаем метод, который будет отслеживать все сообщения, которые будут приходить на наш сокет
     * и реагировать на эти сообщения
     * Метод будет работать отдельным потоком, так как не возможно одновременно
     * слушать и что-то сообщать
     */
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()){
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException e){
                        closeEveryThing(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }
    private void closeEveryThing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
