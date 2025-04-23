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

class MethodContextWriterTest {
    private Path tempFile;
    private MethodContextWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("temp_method_context_test", ".csv");
        writer = new MethodContextWriter(tempFile.toString());
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
}