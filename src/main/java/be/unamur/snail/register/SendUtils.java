package be.unamur.snail.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendUtils {
    private static final String apiURL = "http://localhost:8080/api/v1/constructor-entities";
    private static ConstructorEntityDTO constructorEntityDTO = new ConstructorEntityDTO();

    private SendUtils() {}

    public static String getApiURL() {
        return apiURL;
    }

    public static ConstructorEntityDTO getConstructorEntityDTO() {
        return constructorEntityDTO;
    }

    public static void setConstructorEntityDTO() {
        constructorEntityDTO = new ConstructorEntityDTO();
    }

    public static void prepare(Object fieldInitialization, String constructorSignature, String constructorName, String constructorClassName, String constructorFileName, String attributeName, String attributeType) {
        AttributeEntityDTO attributePayload = new AttributeEntityDTO(attributeName, attributeType);

        if (constructorEntityDTO.isEmpty()) {
            constructorEntityDTO.setName(constructorName);
            constructorEntityDTO.setSignature(constructorSignature);
            constructorEntityDTO.setClassName(constructorClassName);
            constructorEntityDTO.setFileName(constructorFileName);
        }
        constructorEntityDTO.addAttributeEntity(attributePayload);
    }

    public static void send() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(constructorEntityDTO);
            HttpClientService.post(apiURL, json);
        } catch (Exception e) {
            throw new EmptyConstructorEntityDTOException(e);
        }
    }
}
