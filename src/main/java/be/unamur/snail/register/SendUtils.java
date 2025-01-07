package be.unamur.snail.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

public class SendUtils {
    private static String apiUrl = System.getenv("API_URL");
    private static ConstructorEntityDTO constructorEntityDTO;
    private static final Logger log = LoggerFactory.getLogger(SendUtils.class);

    private SendUtils() {}

    public static String getApiURL() {
        return apiUrl;
    }

    public static void setApiURL(String apiURL) {
        apiUrl = apiURL;
    }

    public static ConstructorEntityDTO getConstructorEntityDTO() {
        return constructorEntityDTO;
    }

    public static void initConstructorEntityDTO(String signature, String className, String fileName) {
        constructorEntityDTO = new ConstructorEntityDTO();
        constructorEntityDTO.setSignature(signature);
        constructorEntityDTO.setClassName(className);
        constructorEntityDTO.setFileName(fileName);
    }

    public static void addAttribute(String attributeName, String attributeType, Object actualObject) {
        assert !constructorEntityDTO.isEmpty();
        String actualType = actualObject != null ? actualObject.getClass().getName() : "null";
        AttributeEntityDTO attributePayload = new AttributeEntityDTO(attributeName, attributeType, actualType);
        constructorEntityDTO.addAttributeEntity(attributePayload);
    }

    public static void send() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(constructorEntityDTO);
            HttpClientService.post(apiUrl, json);
        } catch (InvalidPropertiesFormatException e) {
            log.error("Error sending JSON to API : {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error serializing constructorEntityDTO to JSON: {}", e.getMessage());
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("InterruptedException: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("RuntimeException: {}", e.getMessage());
        }
    }
}
