package be.unamur.snail.register;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StackTraceHelperTest {
    @Test
    void getFilteredAndReversedStackTraceTest() {
        StackTraceElement[] fakeStackTrace = {
                new StackTraceElement("org.springframework.boot.ApplicationEnvironment", "createPropertyResolver", "ApplicationEnvironment.java", 43),
                new StackTraceElement("org.springframework.core.env.AbstractEnvironment", "<init>", "AbstractEnvironment.java", 137),
                new StackTraceElement("org.springframework.boot.ApplicationEnvironmentTests", "createEnvironment", "ApplicationEnvironmentTests.java", 30)
        };
        StackTraceProvider mockProvider = () -> fakeStackTrace;
        StackTraceHelper helper = new StackTraceHelper("org.springframework", mockProvider);

        List<StackTraceElement> result = helper.getFilteredAndReversedStackTrace();

        assertEquals(3, result.size());
        assertEquals("createEnvironment", result.get(0).getMethodName());
        assertEquals("<init>", result.get(1).getMethodName());
        assertEquals("createPropertyResolver", result.get(2).getMethodName());
    }
}