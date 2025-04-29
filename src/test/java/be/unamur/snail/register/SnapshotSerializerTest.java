package be.unamur.snail.register;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import be.unamur.snail.register.fixtures.TestModels.*;

class SnapshotSerializerTest {
    private Set<Object> visitedObjects;

    @BeforeEach
    void setUp() {
        visitedObjects = new HashSet<>();
    }

    @AfterEach
    void tearDown() {
        visitedObjects.clear();
    }

    @Test
    void serializePrimitiveTest() {
        assertEquals("10", SnapshotSerializer.serializeToJson(10, visitedObjects));
        assertEquals("true", SnapshotSerializer.serializeToJson(true, visitedObjects));
        assertEquals("\"hello\"", SnapshotSerializer.serializeToJson("hello", visitedObjects));
        assertEquals("3.14", SnapshotSerializer.serializeToJson(3.14, visitedObjects));
    }

    @Test
    void serializeSpecialCharactersTest() {
        assertEquals("\"\\\\\"", SnapshotSerializer.serializeString('\\'));
        assertEquals("\"\\\"${\\\"\"", SnapshotSerializer.serializeString("\"${\""));
    }

    @Test
    void serializeArrayTest() {
        int[] array = {1, 2, 3};
        assertEquals("[1,2,3]", SnapshotSerializer.serializeToJson(array, visitedObjects));

        String[] stringArray = {"a", "b", "c"};
        assertEquals("[\"a\",\"b\",\"c\"]", SnapshotSerializer.serializeToJson(stringArray, visitedObjects));
    }

    @Test
    void serializeArrayWithSpecialCharactersTest() {
        String[] array = {"\\\"", "\\", "-"};
        assertEquals("[\"\\\\\\\"\",\"\\\\\",\"-\"]", SnapshotSerializer.serializeArray(array, visitedObjects));
    }

    @Test
    void serializeEmptyArrayTest() {
        int[] emptyArray = {};
        assertEquals("[]", SnapshotSerializer.serializeToJson(emptyArray, visitedObjects));
    }

    @Test
    void serializeCollectionOfStringsTest() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        assertEquals("[\"one\",\"two\",\"three\"]", SnapshotSerializer.serializeToJson(list, visitedObjects));
    }

    @Test
    void serializeCollectionOfIntegersTest() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        assertEquals("[1,2,3]", SnapshotSerializer.serializeToJson(set, visitedObjects));
    }

    @Test
    void serializeSimpleMapStringTest() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        assertEquals("{\"key1\":\"value1\",\"key2\":\"value2\"}", SnapshotSerializer.serializeToJson(map, visitedObjects));
    }

    @Test
    void serializeSimplePOJOTest() {
        Person person = new Person("John", 42);
        assertEquals("{\"name\": \"John\", \"age\": 42, \"friend\": null}", SnapshotSerializer.serializeToJson(person, visitedObjects));
    }

    @Test
    void getAllFieldsReturnsOwnAndInheritedNonStaticFieldsTest() {
        B b = new B();

        List<Field> actualFieldList = SnapshotSerializer.getAllNonStaticFields(b);

        List<String> fieldNames = actualFieldList.stream()
                        .map(Field::getName)
                        .toList();

        assertThat(fieldNames).containsExactlyInAnyOrder("a1", "a2", "b1", "b2");
    }

    @Test
    void getAllFieldsOnClassWithNoFields() {
        NoFields noFields = new NoFields();
        List<Field> actualFieldList = SnapshotSerializer.getAllNonStaticFields(noFields);
        assertTrue(actualFieldList.isEmpty());
    }

    @Test
    void getAllNonStaticFieldsOnClassWithStaticField() {
        List<Field> actualFieldList = SnapshotSerializer.getNonStaticFieldsFromOneClass(B.class);

        List<String> fieldNames = actualFieldList.stream()
                .map(Field::getName)
                .toList();
        assertThat(fieldNames)
                .containsExactlyInAnyOrder("b1", "b2")
                .doesNotContain("staticB3");
    }

    @Test
    void getAllNonStaticFieldsOnNestedClassWithoutField() {
        List<Field> actualFieldList = SnapshotSerializer.getNonStaticFieldsFromOneClass(MainClass.NestedClassWithoutField.class);

        List<String> fieldNames = actualFieldList.stream()
                .map(Field::getName)
                .toList();
        assertThat(fieldNames).isEmpty();
    }

    @Test
    void getAllNonStaticFieldsOnNestedClassWithoutFieldWithMainClassWithFinalField() {
        List<Field> actualFieldList = SnapshotSerializer.getNonStaticFieldsFromOneClass(MainClassWithFinalField.NestedClassWithoutField.class);

        List<String> fieldNames = actualFieldList.stream()
                .map(Field::getName)
                .toList();
        assertThat(fieldNames).isEmpty();
    }
}