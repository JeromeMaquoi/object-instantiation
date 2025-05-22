package be.unamur.snail.register;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorContextTest {
    @Test
    void attributesToCsvRowWithAttributesTest() {
        AttributeContext attr1 = new AttributeContext("field1", "String", "java.lang.String", "literal");
        AttributeContext attr2 = new AttributeContext("field2", "int", "int", "literal");
        Set<AttributeContext> attributes = Set.of(attr1, attr2);

        ConstructorContext context = new ConstructorContext().withFileName("file.java").withClassName("Class").withMethodName("method").withAttributes(attributes).withParameters(List.of("String", "int"));
        context.setSnapshot("snapshot");

        String result = context.attributesToCsvRow();
        assertTrue(result.startsWith("\""));
        assertTrue(result.contains(attr1.toCsvRow()));
        assertTrue(result.contains(attr2.toCsvRow()));
        assertTrue(result.endsWith("\""));
    }

    @Test
    void toCsvRowOutpusCorrectFormatTest() {
        AttributeContext attr = new AttributeContext("field1", "String", "java.lang.String", "literal");
        StackTraceElement element = new StackTraceElement("com.example.MyClass", "myMethod", "MyClass.java", 42);
        
        ConstructorContext context = new ConstructorContext().withFileName("MyClass.java").withClassName("com.example.MyClass").withMethodName("MyClass").withParameters(List.of("String")).withAttributes(Set.of(attr)).withStackTrace(List.of(element)).withSnapshot("snapshot");

        String row = context.toCsvRow();

        assertTrue(row.contains("MyClass.java"));
        assertTrue(row.contains("com.example.MyClass"));
        assertTrue(row.contains("MyClass/1[String]"));
        assertTrue(row.contains("1"));
        assertTrue(row.contains(attr.toCsvRow()));
        assertTrue(row.contains("com.example.MyClass.myMethod(MyClass.java:42"));
        assertTrue(row.contains("snapshot.json"));
    }
}