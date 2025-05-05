package be.unamur.snail.objectinstantiation;

import be.unamur.snail.register.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendUtilsTest {
    private static Path tempDir;

    @BeforeAll
    static void setUpClass() throws IOException {
        tempDir = Files.createTempDirectory("snapshotTest");
        System.setProperty("SNAPSHOT_DIR", tempDir.toString());
    }

    @BeforeEach
    void setUp() {
        StackTraceHelper mockStackTraceHelper = mock(StackTraceHelper.class);
        when(mockStackTraceHelper.getFilteredStackTrace()).thenReturn(List.of(
                new StackTraceElement("org.springframework.boot.ApplicationEnvironmentTests", "createEnvironment", "ApplicationEnvironmentTests.java", 30)
        ));
        SendUtils.setStackTraceHelper(mockStackTraceHelper);
        SendUtils.setConstructorContext(null);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {}
                });
    }

    @AfterAll
    static void cleanUpClass() throws IOException {
        Files.deleteIfExists(tempDir);
    }

    @Test
    void initConstructorContextStoresCorrectContextTest() {
        SendUtils.initConstructorContext("file.java", "Class", "method", new ArrayList<>(List.of("java.lang.String")));
        ConstructorContext context = SendUtils.getConstructorContext();

        assertNotNull(context);
        assertEquals("file.java", context.getFileName());
        assertEquals("Class", context.getClassName());
        assertEquals("method", context.getMethodName());
        assertEquals(List.of("java.lang.String"), context.getParameters());
    }

    @Test
    void testAddAttributeAddsToConstructorContext() {
        SendUtils.initConstructorContext("file", "Class", "init", List.of());

        SendUtils.addAttribute("field", "String", "hello");

        ConstructorContext context = SendUtils.getConstructorContext();
        assertEquals(1, context.getAttributes().size());

        AttributeContext attr = context.getAttributes().iterator().next();
        assertEquals("field", attr.getName());
        assertEquals("String", attr.getType());
        assertEquals("java.lang.String", attr.getActualType());
    }

    @Test
    void testAddAttributeThrowsIfNotInitialized() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            SendUtils.addAttribute("field", "String", "value");
        });
        assertEquals("ConstructorContext is not initialized", exception.getMessage());
    }

    @Test
    void getSnapshotThrowsIfNotInitialized() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            SendUtils.getSnapshot(new Object());
        });
        assertEquals("ConstructorContext is not initialized", exception.getMessage());
    }
}
