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
    private SnapshotSerializer serializer;

    @BeforeEach
    void setUp() {
        visitedObjects = new HashSet<>();
        serializer = new SnapshotSerializer(3);
    }

    @AfterEach
    void tearDown() {
        visitedObjects.clear();
    }

    @Test
    void serializePrimitiveTest() {
        assertEquals("10", serializer.serializeToJson(10));
        assertEquals("true", serializer.serializeToJson(true));
        assertEquals("3.14", serializer.serializeToJson(3.14));
    }

    @Test
    void serializeSpecialCharactersTest() {
        assertEquals("\"\\\\\"", serializer.serializeToJson('\\'));
        assertEquals("\"\\\"${\\\"\"", serializer.serializeToJson("\"${\""));
    }

    @Test
    void serializeArrayTest() {
        int[] array = {1, 2, 3};
        assertEquals("[1,2,3]", serializer.serializeToJson(array));

        String[] stringArray = {"a", "b", "c"};
        assertEquals("[\"a\",\"b\",\"c\"]", serializer.serializeToJson(stringArray));
    }

    @Test
    void serializeArrayWithSpecialCharactersTest() {
        String[] array = {"\\\"", "\\", "-"};
        assertEquals("[\"\\\\\\\"\",\"\\\\\",\"-\"]", serializer.serializeToJson(array));
    }

    @Test
    void serializeEmptyArrayTest() {
        int[] emptyArray = {};
        assertEquals("[]", serializer.serializeToJson(emptyArray));
    }

    @Test
    void serializeCollectionOfStringsTest() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        assertEquals("[\"one\",\"two\",\"three\"]", serializer.serializeToJson(list));
    }

    @Test
    void serializeCollectionOfIntegersTest() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        assertEquals("[1,2,3]", serializer.serializeToJson(set));
    }

    @Test
    void serializeSimpleMapStringTest() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        assertEquals("{\"key1\":\"value1\",\"key2\":\"value2\"}", serializer.serializeToJson(map));
    }

    @Test
    void serializeSimplePOJOTest() {
        Person person = new Person("John", 42);
        assertEquals("{\"name\":\"John\",\"age\":42,\"friend\":null}", serializer.serializeToJson(person));
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

    @Test
    void containsReferenceWithBooleanTest() {
        visitedObjects.add(true);

        assertFalse(SnapshotSerializer.containsReference(true, visitedObjects));
    }

    @Test
    void containsReferenceWithStringTest() {
        String str1 = "test";
        visitedObjects.add(str1);
        assertFalse(SnapshotSerializer.containsReference("test", visitedObjects));
    }

    @Test
    void serializeComplicateEnumTest() {
        EnumComplicate testEnumComplicate = EnumComplicate.KILOMETERS;
        assertEquals("\"KILOMETERS\"", serializer.serializeToJson(testEnumComplicate));
    }

    @Test
    void serializeComplicateEnumInitializedInAnotherClassTest() {
        SampleEnumObject obj = new SampleEnumObject("name", EnumComplicate.KILOMETERS);
        assertDoesNotThrow(() -> {
            String json = serializer.serializeToJson(obj);
            assertTrue(json.contains("KILOMETERS"));
        });
    }

    @Test
    void buildSnapshotNullObjectTest() {
        Object result = serializer.buildSnapshot(null, 0);
        assertNull(result);
    }

    @Test
    void buildSnapshotPrimitiveValueTest() {
        Object result = serializer.buildSnapshot(42, 0);
        assertEquals(42, result);
    }

    @Test
    void buildSnapshotStringValueTest() {
        Object result = serializer.buildSnapshot("hello", 0);
        assertEquals("hello", result);
    }

    @Test
    void buildSnapshotSimpleObjectTest() {
        Person person = new Person("John", 42);
        Object actualObject = serializer.buildSnapshot(person, 0);
        assertInstanceOf(Map.class, actualObject);

        Map<String, Object> map = (Map<String, Object>) actualObject;

        assertEquals(Person.class.getName(), map.get("_class"));
        assertTrue(map.containsKey("name"));
        assertEquals("John", map.get("name"));
        assertEquals(42, map.get("age"));
    }
}