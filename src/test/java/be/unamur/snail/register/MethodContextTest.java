package be.unamur.snail.register;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MethodContextTest {
    private String getMethodName(String methodName, int nbParams, String params) {
        return "\"" + methodName + "/" + nbParams + "[" + params + "]" + "\"";
    }

    @Test
    void toCsvRowWithParametersTest() {
        String fileName = "ApplicationEnvironment.java";
        String className = "org.springframework.boot.ApplicationEnvironment";
        String methodName = "createPropertyResolver";
        List<String> parameters = Arrays.asList("String", "int");
        List<StackTraceElement> stackTrace = Arrays.asList(
                new StackTraceElement("org.springframework.boot.ApplicationEnvironmentTests", "createEnvironment", "ApplicationEnvironmentTests.java", 30),
                new StackTraceElement("org.springframework.boot.ApplicationEnvironment", "createPropertyResolver", "ApplicationEnvironment.java", 43)
        );

        MethodContext methodContext = new MethodContext(fileName, className, methodName, parameters, stackTrace);

        String expectedTrace = "\"org.springframework.boot.ApplicationEnvironmentTests.createEnvironment(ApplicationEnvironmentTests.java:30),org.springframework.boot.ApplicationEnvironment.createPropertyResolver(ApplicationEnvironment.java:43)\"";
        String expectedMethodName = getMethodName(methodName, 2, "String,int");
        String expectedRow = String.format("%s,%s,%s,%s", fileName, className, expectedMethodName,expectedTrace);

        assertEquals(expectedRow, methodContext.toCsvRow());
    }

    @Test
    void toCsvRowWithoutParametersTest() {
        String fileName = "ApplicationEnvironment.java";
        String className = "org.springframework.boot.ApplicationEnvironment";
        String methodName = "createPropertyResolver";
        List<String> parameters = Arrays.asList();
        List<StackTraceElement> stackTrace = Arrays.asList(
                new StackTraceElement("org.springframework.boot.ApplicationEnvironmentTests", "createEnvironment", "ApplicationEnvironmentTests.java", 30),
                new StackTraceElement("org.springframework.boot.ApplicationEnvironment", "createPropertyResolver", "ApplicationEnvironment.java", 43)
        );

        MethodContext methodContext = new MethodContext(fileName, className, methodName, parameters, stackTrace);

        String expectedTrace = "\"org.springframework.boot.ApplicationEnvironmentTests.createEnvironment(ApplicationEnvironmentTests.java:30),org.springframework.boot.ApplicationEnvironment.createPropertyResolver(ApplicationEnvironment.java:43)\"";
        String expectedMethodName = "\"" + methodName + "/" + 0 + "\"";
        String expectedRow = String.format("%s,%s,%s,%s", fileName, className, expectedMethodName,expectedTrace);

        assertEquals(expectedRow, methodContext.toCsvRow());
    }
}