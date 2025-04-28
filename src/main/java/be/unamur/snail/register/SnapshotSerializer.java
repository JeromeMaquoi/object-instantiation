package be.unamur.snail.register;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class SnapshotSerializer {
    private SnapshotSerializer() {}

    public static String serializeToJson(Object object, Set<Object> visitedObjects) {
        if (object == null) return "null";
        if (visitedObjects.contains(object)) return "\"<circular reference>\"";
        visitedObjects.add(object);

        Class<?> clazz = object.getClass();

        System.out.println("\nclazz: " + clazz);
        System.out.println("object: " + object);
        if (isPrimitive(object)) {
            return serializePrimitive(object);
        } else if (isString(object)) {
            return serializeString(object);
        } else if (clazz.isArray()) {
            return serializeArray(object, visitedObjects);
        } else if (object instanceof Collection) {
            return serializeCollection((Collection<?>) object, visitedObjects);
        } else if (object instanceof Map) {
            return serializeMap((Map<?, ?>) object, visitedObjects);
        } else {
            try {
                return serializePOJO(object, clazz, visitedObjects);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isPrimitive(Object object) {
        return object instanceof Number || object instanceof Boolean;
    }

    public static boolean isString(Object object) {
        return object instanceof String || object instanceof Character;
    }

    public static String serializePrimitive(Object object) {
        return object.toString();
    }

    public static String serializeString(Object object) {
        System.out.println("string : " + object);
        return "\"" + object.toString() + "\"";
    }

    public static String serializeArray(Object object, Set<Object> visitedObjects) {
        int length = Array.getLength(object);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < length; i++) {
            Object item = Array.get(object, i);
            sb.append(serializeToJson(item, visitedObjects));
            if (i < length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String serializeCollection(Collection<?> collection, Set<Object> visitedObjects) {
        StringBuilder sb = new StringBuilder("[");
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            sb.append(serializeToJson(iterator.next(), visitedObjects));
            if (iterator.hasNext()) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String serializeMap(Map<?, ?> map, Set<Object> visitedObjects) {
        StringBuilder sb = new StringBuilder("{");

        List<Map.Entry<?,?>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(e -> e.getKey().toString()));

        Iterator<?> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
            sb.append("\"").append(String.valueOf(entry.getKey())).append("\":").append(serializeToJson(entry.getValue(), visitedObjects));
            if (iterator.hasNext()) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public static String serializePOJO(Object object, Class<?> clazz, Set<Object> visitedObjects) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder("{");
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(object);
            sb.append("\"").append(field.getName()).append("\": ");
            sb.append(serializeToJson(value, visitedObjects));
            sb.append(", ");
        }
        if (sb.toString().endsWith(", ")) sb.setLength(sb.length() - 2);
        sb.append("}");
        return sb.toString();
    }
}
