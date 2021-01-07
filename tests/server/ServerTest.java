package server;

import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import server.data.ServerData;

public class ServerTest {

    private ServerData serverData;

    @Before
    public void setUp() {
        serverData = new ServerData();
    }

    @Test
    public void test() {
        ServerData serverDataMock = Mockito.mock(ServerData.class);
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        
        //doNothing().when(serverDataMock).registerUser("myUsername", "myPassword", "myFirstName", "myLastName", "myEmail", socketChannelMock));
    }
}
