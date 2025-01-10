package be.unamur.snail.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

public class SendUtils {
    private static String apiUrl = System.getenv("API_URL");
    private static String PROJECT_PACKAGE_PREFIX = System.getenv("PROJECT_PACKAGE_PREFIX");
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

    public static void setCallerContext(String constructorName, Object obj) {
        List<StackTraceElement> projectStackTrace = Arrays.stream(Thread.currentThread().getStackTrace())
                .filter(element -> element.getClassName().startsWith(PROJECT_PACKAGE_PREFIX))
                .filter(element -> !element.getClassName().contains(constructorName))
                .toList();

        System.out.println("Stack trace for constructor: " + constructorName);
        for (StackTraceElement element : projectStackTrace) {
            System.out.printf("    at %s.%s(%s:%d)%n",
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber());
        }

        if (obj == null) {
            log.warn("Object is null.");
            return;
        }

        Class<?> clazz = obj.getClass();
        System.out.println("Capturing attribute values for class: " + clazz.getName());

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true); // Access private fields
            try {
                Object value = field.get(obj);
                System.out.printf("    %s (%s) = %s%n",
                        field.getName(),
                        field.getType().getName(),
                        value != null ? value.toString() : "null"
                );
            } catch (IllegalAccessException e) {
                System.out.printf("    Unable to access field: %s%n", field.getName());
            }
        }
        System.out.println("\n\n");
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
