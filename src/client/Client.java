package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 7777;
    private static final String LOCAL_HOST = "localhost";
    private static final String QUIT = "quit";

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
                BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8")); // server input
                PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true); // server output
                Scanner scanner = new Scanner(System.in)) { // console input

            socketChannel.connect(new InetSocketAddress(LOCAL_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");

            // thread
            ClientRunnable clientRunnable = new ClientRunnable(socketChannel);
            Thread newClientThread = new Thread(clientRunnable);
            newClientThread.start();

            while (true) {

                String message = scanner.nextLine(); // read a line from the console

                if (QUIT.equals(message)) {
                    break;

                }

                writer.println(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
