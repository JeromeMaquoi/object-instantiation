package be.unamur.snail.register;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorContextTest {
    @Test
    void attributesToCsvRowWithAttributesTest() {
        AttributeContext attr1 = new AttributeContext("field1", "String", "java.lang.String");
        AttributeContext attr2 = new AttributeContext("field2", "int", "int");
        Set<AttributeContext> attributes = Set.of(attr1, attr2);

        ConstructorContext context = new ConstructorContext("file.java", "Class","method", List.of("String", "int"), attributes, List.of());
        context.setSnapshotFilePath("snapshot.json");

        String result = context.attributesToCsvRow();
        assertTrue(result.startsWith("\""));
        assertTrue(result.contains(attr1.toCsvRow()));
        assertTrue(result.contains(attr2.toCsvRow()));
        assertTrue(result.endsWith("\""));
    }

    @Test
    void toCsvRowOutpusCorrectFormatTest() {
        AttributeContext attr = new AttributeContext("field1", "String", "java.lang.String");
        StackTraceElement element = new StackTraceElement("com.example.MyClass", "myMethod", "MyClass.java", 42);

        ConstructorContext context = new ConstructorContext(
                "MyClass.java",
                "com.example.MyClass",
                "MyClass",
                List.of("String"),
                Set.of(attr),
                List.of(element)
        );
        context.setSnapshotFilePath("snapshot.json");

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