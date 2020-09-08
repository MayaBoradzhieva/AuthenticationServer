package logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import server.enums.DirectoriesEnum;

public class ExceptionLogger {

    public void writeToExceptionLogger(Exception exceptionThrown) {
        try (BufferedWriter exceptionLoggerFile = new BufferedWriter(
                new FileWriter(DirectoriesEnum.EXCEPTION_LOGGER_DIR.getDirectory(), true));
                PrintWriter pWriter = new PrintWriter(exceptionLoggerFile, true);) {

            exceptionThrown.printStackTrace(pWriter);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
