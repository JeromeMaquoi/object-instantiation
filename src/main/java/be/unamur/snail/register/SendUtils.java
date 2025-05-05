package be.unamur.snail.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SendUtils {
    private static String CSV_HEADER_METHOD = "file,class,method,stacktrace\n";
    private static String CSV_HEADER_CONSTRUCTOR = "file,class,constructor,attributesQty,attributes,stacktrace,snapshot\n";
    public static EnvVariables envVariables = new EnvVariables();
    private static StackTraceHelper stackTraceHelper = new StackTraceHelper(envVariables.getEnvVariable("PROJECT_PACKAGE_PREFIX"), new DefaultStackTraceProvider());

    private static ConstructorContext constructorContext;
    private static final Logger log = LoggerFactory.getLogger(SendUtils.class);

    private SendUtils() {}

    // Injector for tests
    public static void setStackTraceHelper(StackTraceHelper newHelper) {
        stackTraceHelper = newHelper;
    }


    public static ConstructorContext getConstructorContext() {
        return constructorContext;
    }

    public static void setConstructorContext(ConstructorContext newConstructorContext) {
        constructorContext = newConstructorContext;
    }

    public static void initConstructorContext(String fileName, String className, String methodName, List<String> parameters) {
        constructorContext = new ConstructorContext().withFileName(fileName).withClassName(className).withMethodName(methodName).withParameters(parameters).withAttributes(new HashSet<>());
    }

    public static void addAttribute(String attributeName, String attributeType, Object actualObject) {
        if (constructorContext == null || constructorContext.isEmpty()) {
            throw new IllegalStateException("ConstructorContext is not initialized");
        }
        String actualType = actualObject != null ? actualObject.getClass().getName() : "null";
        AttributeContext attributePayload = new AttributeContext(attributeName, attributeType, actualType);
        constructorContext.addAttribute(attributePayload);
    }

    public static void getSnapshot(Object obj) {
        if (constructorContext == null || constructorContext.isEmpty()) {
            throw new IllegalStateException("ConstructorContext is not initialized");
        }
        try {
            List<StackTraceElement> stackTrace = stackTraceHelper.getFilteredStackTrace();
            constructorContext = constructorContext.withStackTrace(stackTrace);
            Path filePath = prepareSnapshotFilePath();
            String json = SnapshotSerializer.serializeToJson(obj, new HashSet<>());
            writeJsonToFile(filePath, json);
            constructorContext.setSnapshotFilePath(filePath.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path prepareSnapshotFilePath() throws IOException {
        String fileName = UUID.randomUUID() + ".json";
        Path snapshotDir = Paths.get(envVariables.getEnvVariable("SNAPSHOT_DIR"));
        Files.createDirectories(snapshotDir);
        return snapshotDir.resolve(fileName);
    }

    public static void writeJsonToFile(Path filePath, String json) throws IOException {
        Files.writeString(filePath, json);
    }

    public static void writeConstructorContext() {
        ContextWriter<ConstructorContext> writer = new ContextWriter<>(envVariables.getEnvVariable("CSV_CONSTRUCTOR_FILE_PATH"));

        try {
            ensureHeaderWritten(envVariables.getEnvVariable("CSV_CONSTRUCTOR_FILE_PATH"), CSV_HEADER_CONSTRUCTOR);
            writer.writeIfNotExists(constructorContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }






    public static void setMethodContext(String fileName, String className, String methodName, List<String> parameters) {
        List<StackTraceElement> stackTrace = stackTraceHelper.getFilteredStackTrace();
        MethodContext context = new MethodContext(fileName, className, methodName, parameters, stackTrace);
        ContextWriter<MethodContext> writer = new ContextWriter<>(envVariables.getEnvVariable("CSV_METHOD_FILE_PATH"));
        try {
            ensureHeaderWritten(envVariables.getEnvVariable("CSV_METHOD_FILE_PATH"), CSV_HEADER_METHOD);
            writer.writeIfNotExists(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureHeaderWritten(String csvFilePath, String header) throws IOException {
        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            Files.write(Paths.get(csvFile.toURI()), header.getBytes());
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
    

    /*private static void printFields(Object obj, int depth) {
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
    }*/
}
