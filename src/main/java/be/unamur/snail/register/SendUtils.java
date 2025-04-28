package be.unamur.snail.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SendUtils {
    private static String apiUrl = System.getenv("API_URL");
    private static String PROJECT_PACKAGE_PREFIX = System.getenv("PROJECT_PACKAGE_PREFIX");
    private static String CSV_HEADER = "file,class,method,stacktrace\n";
    private static MethodContextWriter writer = new MethodContextWriter(System.getenv("CSV_FILE_PATH"));
    private static final StackTraceHelper stackTraceHelper = new StackTraceHelper(System.getenv("PROJECT_PACKAGE_PREFIX"), new DefaultStackTraceProvider());

    private static ConstructorEntityDTO constructorEntityDTO;
    private static final Logger log = LoggerFactory.getLogger(SendUtils.class);

    private SendUtils() {}

    public static String getApiURL() {
        return apiUrl;
    }

    public static void setApiURL(String apiURL) {
        apiUrl = apiURL;
    }

    public static void setProjectPackagePrefix(String projectPackagePrefix) {
        PROJECT_PACKAGE_PREFIX = projectPackagePrefix;
    }

    public static void setWriter(MethodContextWriter customWriter) {
        writer = customWriter;
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

    public static void setMethodContext(String fileName, String className, String methodName, List<String> parameters) {
        List<StackTraceElement> stackTrace = stackTraceHelper.getFilteredStackTrace();
        MethodContext context = new MethodContext(fileName, className, methodName, parameters, stackTrace);
        try {
            ensureHeaderWritten();
            writer.writeIfNotExists(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureHeaderWritten() throws IOException {
        File csvFile = new File(System.getenv("CSV_FILE_PATH"));
        if (!csvFile.exists()) {
            Files.write(Paths.get(csvFile.toURI()), CSV_HEADER.getBytes());
        }
    }

    /*private static StackTraceSnapshotElementDTO createStackTraceSnapShotElementDTO(String fileName, String className, String methodName, List<String> parameters) {
        List<StackTraceElement> projectStackTrace = getStackTrace();

        StackTraceElement parent = projectStackTrace.get(projectStackTrace.size()-2);
        StackTraceElement currentElement = projectStackTrace.get(projectStackTrace.size()-1);
        int lineNumber = currentElement.getLineNumber();
        MethodElementDTO currentMethodElement = new MethodElementDTO().withFileName(fileName).withClassName(className).withMethodName(methodName).isConstructor(isConstructor(currentElement)).withParameters(parameters);

        projectStackTrace.remove(projectStackTrace.size()-1);
        StackTraceSnapshotElementDTO stackTraceSnapshotElementDTO = new StackTraceSnapshotElementDTO();
        for (StackTraceElement element : projectStackTrace) {
            System.out.printf("    at %s.%s(%s:%d)%n",
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber());
            stackTraceSnapshotElementDTO.addAncestorStackTraceElementDTO(createStackTraceElementDTO(element));
        }
        stackTraceSnapshotElementDTO.setParent(createStackTraceElementDTO(parent));
        stackTraceSnapshotElementDTO.setLineNumber(lineNumber);
        stackTraceSnapshotElementDTO.setMethod(currentMethodElement);

        return stackTraceSnapshotElementDTO;
    }*/

    public static void setCallerContext(String constructorName, Object obj) {
        /*List<StackTraceElement> projectStackTrace = getStackTrace();

        StackTraceDTO stackTraceDTO = createStackTraceDTO(projectStackTrace);
//        System.out.println("\n");
//        System.out.println(stackTraceDTO);
//        System.out.println("\n");

        printFields(obj, 0);

//        System.out.println("\n\n");*/
    }

    private static StackTraceDTO createStackTraceDTO(List<StackTraceElement> projectStackTrace) {
        StackTraceDTO stackTraceDTO = new StackTraceDTO();
//        System.out.println("Stack trace for constructor: " + constructorName);
        for (StackTraceElement element : projectStackTrace) {
            /*System.out.printf("    at %s.%s(%s:%d)%n",
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber());*/
            stackTraceDTO.addStackTraceElement(createStackTraceElementDTO(element));
        }
        return stackTraceDTO;
    }

    private static StackTraceElementDTO createStackTraceElementDTO(StackTraceElement stackTraceElement) {
        MethodElementDTO methodElementDTO = createMethodElementDTO(stackTraceElement);
        return new StackTraceElementDTO().withMethod(methodElementDTO).withLineNumber(stackTraceElement.getLineNumber());
    }

    private static MethodElementDTO createMethodElementDTO(StackTraceElement element) {
        return new MethodElementDTO().withFileName(element.getFileName()).withClassName(element.getClassName()).withMethodName(getMethodName(element)).isConstructor(isConstructor(element));
    }

    private static String getMethodName(StackTraceElement element) {
        if (element.getMethodName().equals("<init>")) {
            String fullClassName = element.getClassName();
            return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        }
        return element.getMethodName();
    }

    private static boolean isConstructor(StackTraceElement element) {
        return element.getMethodName().equals("<init>");
    }
    

    private static void printFields(Object obj, int depth) {
        if (obj == null) {
            log.warn("Object is null.");
            return;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String indent = "    ".repeat(depth);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true); // Access private fields
            try {
                Object value = field.get(obj);
                System.out.printf("%s    %s  (%s) = %s%n",
                        indent,
                        field.getName(),
                        field.getType().getName(),
                        value != null ? value.toString() : "null"
                );

                if (value instanceof Collection<?> collection) {
                    for (Object item : collection) {
                        System.out.println(indent + "[Collection Item]:");
                        printFields(item, depth + 2);
                    }
                }

                else if (value instanceof Map<?,?> map) {
                    for (Map.Entry<?,?> entry : map.entrySet()) {
                        System.out.println(indent + "  [Map Entry]:");
                        System.out.println(indent + "    Key:");
                        printFields(entry.getKey(), depth + 3);
                        System.out.println(indent + "    Value:");
                        printFields(entry.getValue(), depth + 3);
                    }
                }

                else if (value != null && !field.getType().isPrimitive() && !field.getType().getName().startsWith("java.lang")) {
                    printFields(value, depth + 1);
                }
            } catch (IllegalAccessException e) {
                System.out.printf("    Unable to access field: %s%n", field.getName());
            }
        }
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
//            HttpClientService.post(apiUrl, json);
//        } catch (InvalidPropertiesFormatException e) {
//            log.error("Error sending JSON to API : {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error serializing constructorEntityDTO to JSON: {}", e.getMessage());
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
//        } catch (InterruptedException e) {
//            log.error("InterruptedException: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("RuntimeException: {}", e.getMessage());
        }
    }
}
