package be.unamur.snail.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterUtils {
    private final static Logger log = LoggerFactory.getLogger(RegisterUtils.class);
    private static final String LOG_FILE_PATH = "log_output.txt";

    public static void register(Object currentObject, Object fieldInitialization) {
        String content = String.format("Current object : %s, field initialization : %s", currentObject, fieldInitialization);
        log.info(content);
        writeToFile(content);
    }

    private static void writeToFile(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
