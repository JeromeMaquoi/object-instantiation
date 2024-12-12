package be.unamur.snail.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RegisterUtils {
    private final static Logger log = LoggerFactory.getLogger(RegisterUtils.class);
    private static final String LOG_FILE_PATH = "log_output.txt";
    private static final String CSV_FILE_PATH = "attributes_assignments.csv";

    public static void register(Object currentObject, Object fieldInitialization, String constructorName, String fieldName, String fieldType) {
        String content = String.format("Constructor : %s, field name : %s, field type : %s", constructorName, fieldName, fieldType);
        log.info(content);
        writeAttributesToCsv(constructorName, fieldName, fieldType);
    }

    private static void writeAttributesToCsv(String constructorName, String fieldName, String fieldType) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
            File csvFile = new File(CSV_FILE_PATH);
            if (csvFile.length() == 0) {
                writer.write("Constructor signature,Attribute name,Attribute type");
                writer.newLine();
            }
            writer.write(String.format("\"%s\",\"%s\",\"%s\"", constructorName, fieldName, fieldType));
            writer.newLine();
        } catch (IOException e) {
            log.error("Failed to write CSV file: {}", e.getMessage());
        }
    }
}
