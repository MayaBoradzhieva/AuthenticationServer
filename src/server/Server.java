package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import commands.CommandExecutor;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;

public class Server {
    private static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 7777;
    private static final int BUFFER_SIZE = 1024;
    private static final int SLEEP_MILLIS = 200;

    private ServerData serverData;
    private CommandExecutor commandExecutor;

    public Server() {
        serverData = new ServerData();
        commandExecutor = new CommandExecutor();
    }

    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                int readyChannels = selector.select(); // number of keys which operations were updated
                if (readyChannels == 0) {
                    System.out.println("Still waiting for a ready channel...");
                    try {
                        Thread.sleep(SLEEP_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys(); // returns the selector's key-set
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        buffer.clear();
                        int readBytes = socketChannel.read(buffer); // returns the bytes read
                        if (readBytes <= 0) {
                            System.out.println("Nothing to read, will close channel.");
                            socketChannel.close();
                            break;
                        }

                        buffer.flip();

                        // processing message
                        String line = new String(buffer.array(), 0, buffer.limit());

                        try {
                            commandExecutor.processLine(line, serverData, socketChannel);
                        } catch (InvalidCommandException e) {
                            e.printStackTrace();
                        }

                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove(); // removes the last element returned by this iterator
                }

            }
        } catch (IOException e) {
            System.out.println("There is a problem with the server socket.");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}