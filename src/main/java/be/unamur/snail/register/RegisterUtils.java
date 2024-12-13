package be.unamur.snail.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterUtils {
    private final static Logger log = LoggerFactory.getLogger(RegisterUtils.class);
    private static final String LOG_FILE_PATH = "log_output.txt";
    private static final String CSV_FILE_PATH = "attributes_assignments.csv";
    private static final String apiURL = "http://localhost:8080/api/v1/constructor-attributes";

    public static void register(Object currentObject, Object fieldInitialization, String constructorSignature, String attributeName, String attributeType) {
        String content = String.format("Constructor : %s, field name : %s, field type : %s", constructorSignature, attributeName, attributeType);
        log.info(content);
//        writeAttributesToCsv(constructorSignature, attributeName, attributeType);
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("constructorSignature", constructorSignature);
            payload.put("attributeName", attributeName);
            payload.put("attributeType", attributeType);

            log.info(HttpClientService.post(apiURL, payload));
        } catch (Exception e) {
            log.error("Error while calling the API : {}", e.getMessage());
        }
    }

    private void writeAttributesToCsv(String constructorName, String fieldName, String fieldType) {
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

    static class HttpClientService {
        private static HttpClient httpClient;
        private static ObjectMapper objectMapper;

        HttpClientService() {
            httpClient = HttpClient.newHttpClient();
            this.objectMapper = new ObjectMapper();
        }

        public static String post(String url, Object payload) throws Exception {
            // Convert the payload to JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Send the request and return the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                throw new RuntimeException("HTTP error: " + response.statusCode() + ", body: " + response.body());
            }
        }
    }
}
