package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientRunnable implements Runnable {

    private static final String DISCONNECTED = "disconnected";
    private static final int BUFFER_SIZE = 1024;
    private SocketChannel socketChannel;

    public ClientRunnable(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                buffer.clear();
                socketChannel.read(buffer); // buffer fill
                buffer.flip();
                String reply = new String(buffer.array(), 0, buffer.limit()); // buffer drain
                if (reply.equals(DISCONNECTED)) {
                    break;
                }
                System.out.println(reply);
            }
        } catch (IOException e) {
            System.out.println("Failed to read the message");
        }
    }

}
