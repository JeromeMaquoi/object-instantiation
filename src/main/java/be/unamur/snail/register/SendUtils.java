package be.unamur.snail.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class SendUtils {
    private static final Logger log = LoggerFactory.getLogger(SendUtils.class);
    private static final String apiURL = "http://localhost:8080/api/v1/constructor-entities";

    private SendUtils() {}

    public static void prepare(Object fieldInitialization, String constructorSignature, String constructorName, String constructorClassName, String constructorFileName, String attributeName, String attributeType) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("constructorSignature", constructorSignature);
            payload.put("constructorName", constructorName);
            payload.put("constructorClassName", constructorClassName);
            payload.put("constructorFileName", constructorFileName);
            payload.put("attributeName", attributeName);
            payload.put("attributeType", attributeType);
            HttpClientService.post(apiURL, payload);
        } catch (Exception e) {
            log.error("Error while calling the API : {}", e.getMessage());
        }
    }

    /*private void writeAttributesToCsv(String constructorName, String fieldName, String fieldType) {
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
    }*/
}
