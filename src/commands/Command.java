package commands;

import java.nio.channels.SocketChannel;

import commands.exceptions.InvalidCommandException;
import server.data.ServerData;

public interface Command {

    void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException;

}
