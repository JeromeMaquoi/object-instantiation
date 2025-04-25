package be.unamur.snail.register;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContextWriterTest {
    private Path tempFile;
    private ContextWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("temp_method_context_test", ".csv");
        writer = new ContextWriter(tempFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void writeIfNotExistsMethodContextTest() throws IOException {
        MethodContext context = new MethodContext(
                "file",
                "class",
                "method",
                Arrays.asList("String", "String"),
                Arrays.asList(
                        new StackTraceElement("org.springframework.boot.ApplicationEnvironmentTests", "createEnvironment", "ApplicationEnvironmentTests.java", 30),
                        new StackTraceElement("org.springframework.boot.ApplicationEnvironment", "createPropertyResolver", "ApplicationEnvironment.java", 43)
                )
        );

        writer.writeIfNotExists(context);

        List<String> lines = Files.readAllLines(tempFile);
        String expectedTrace = "\"org.springframework.boot.ApplicationEnvironmentTests.createEnvironment(ApplicationEnvironmentTests.java:30),org.springframework.boot.ApplicationEnvironment.createPropertyResolver(ApplicationEnvironment.java:43)\"";
        String expectedLine = String.format("%s,%s,%s,%s","file","class","\"method/2[String,String]\"",expectedTrace);
        assertEquals(1, lines.size());
        assertEquals(expectedLine, lines.get(0));
    }

    @Test
    void splitCstLineWithQuotedFieldsContainingCommasTest() {
        String line = "/path/to/file,com.example.MyClass,\"myMethod/2[java.lang.String,int]\",\"com.example.MyClass.methodA(MyClass.java:10),com.example.Other.methodB(Other.java:20)\"";
        String[] expected = {
                "/path/to/file",
                "com.example.MyClass",
                "myMethod/2[java.lang.String,int]",
                "com.example.MyClass.methodA(MyClass.java:10),com.example.Other.methodB(Other.java:20)"
        };

        String[] actual = writer.splitCsvLine(line);
        assertArrayEquals(expected, actual);
    }

    @Test
    void writeIfNotExistsWriteOnlyOnceTest() throws IOException {
        MethodContext context = new MethodContext(
                "/path/to/MyClass.java",
                "com.example.MyClass",
                "doSomething",
                List.of("java.lang.String"),
                List.of(new StackTraceElement("com.example.MyClass", "doSomething", "MyClass.java", 10))
        );
        writer.writeIfNotExists(context);
        writer.writeIfNotExists(context);

        List<String> lines = Files.readAllLines(tempFile);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("doSomething/1[java.lang.String]"), "Method with args should be written");
    }
}