package be.unamur.snail.register;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SendUtils {
    private static final String apiURL = "http://localhost:8080/api/v1/constructor-entities";
    private static ConstructorEntityDTO constructorEntityDTO;

    private SendUtils() {}

    public static String getApiURL() {
        return apiURL;
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

    public static void addAttribute(String attributeName, String attributeType) {
        assert !constructorEntityDTO.isEmpty();
        AttributeEntityDTO attributePayload = new AttributeEntityDTO(attributeName, attributeType);
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
