package server.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import server.enums.AdminOperations;
import server.enums.DirectoriesEnum;
import server.enums.EventTypeEnum;
import server.enums.OperationResults;

public class AuditLog {
    private static final String TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final String TEXT_FORMAT_LOGIN = "[%s] %s %s %s";
    private static final String TEXT_FORMAT_CONFIG_CHANGE = "[%s] %s %s %s %s %s %s%n";

    public static String getCurrentTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return localDateTime.format(formatter);
    }

    private String getIPAddress(SocketChannel socketChannel) {
        return socketChannel.socket().getInetAddress().toString();
    }

    private void writeToAuditLog(String eventInfo) {
        try (BufferedWriter auditLogFile = new BufferedWriter(
                new FileWriter(DirectoriesEnum.AUDIT_LOG_DIR.getDirectory(), true));) {
            auditLogFile.write(eventInfo);
            auditLogFile.flush();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void logFailedLogin(String authorUsername, SocketChannel socketChannel) {

        String IPAddress = getIPAddress(socketChannel);
        String eventInfo = String.format(TEXT_FORMAT_LOGIN, getCurrentTime(), EventTypeEnum.FAILED_LOGIN.getEventType(),
                authorUsername, IPAddress);

        writeToAuditLog(eventInfo);
    }

    public void logStartOfConfigurationChange(long operationID, String authorUsername, String affectedUser,
            AdminOperations madeChange, SocketChannel socketChannel) {

        String IPAddress = getIPAddress(socketChannel);

        String eventInfo = String.format(TEXT_FORMAT_CONFIG_CHANGE, getCurrentTime(), operationID,
                EventTypeEnum.CONFIGURATION_CHANGE.getEventType(), authorUsername, IPAddress, affectedUser,
                madeChange.getOperation());

        writeToAuditLog(eventInfo);
    }

    public void logResultOfConfigurationChange(long operationID, String authorUsername, String affectedUser,
            OperationResults result, SocketChannel socketChannel) {
        String IPAddress = getIPAddress(socketChannel);

        String eventInfo = String.format(TEXT_FORMAT_CONFIG_CHANGE, getCurrentTime(), operationID,
                EventTypeEnum.CONFIGURATION_CHANGE.getEventType(), authorUsername, IPAddress, affectedUser,
                result.getResult());

        writeToAuditLog(eventInfo);
    }

}
