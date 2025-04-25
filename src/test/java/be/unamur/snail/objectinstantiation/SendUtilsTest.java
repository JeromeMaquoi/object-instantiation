package be.unamur.snail.objectinstantiation;

import be.unamur.snail.register.AttributeContext;
import be.unamur.snail.register.ConstructorContext;
import be.unamur.snail.register.SendUtils;
import be.unamur.snail.register.StackTraceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SendUtilsTest {
    @BeforeEach
    void setUp() {
        StackTraceHelper mockStackTraceHelper = mock(StackTraceHelper.class);
        when(mockStackTraceHelper.getFilteredAndReversedStackTrace()).thenReturn(List.of(
                new StackTraceElement("org.springframework.boot.ApplicationEnvironmentTests", "createEnvironment", "ApplicationEnvironmentTests.java", 30)
        ));
        SendUtils.setStackTraceHelper(mockStackTraceHelper);
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
        assertFalse(context.getStackTrace().isEmpty());
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
}
