package be.unamur.snail.register;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotSerializerTest {
    private Set<Object> visitedObjects;

    // POJO class for testing
    public static class Person {
        private String name;
        private int age;
        private Person friend;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public void setFriend(Person friend) {
            this.friend = friend;
        }
    }

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
    void serializeArrayTest() {
        int[] array = {1, 2, 3};
        assertEquals("[1,2,3]", SnapshotSerializer.serializeToJson(array, visitedObjects));

        String[] stringArray = {"a", "b", "c"};
        assertEquals("[\"a\",\"b\",\"c\"]", SnapshotSerializer.serializeToJson(stringArray, visitedObjects));
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
}