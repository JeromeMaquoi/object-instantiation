package be.unamur.snail.register;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SnapshotSerializer {
    private SnapshotSerializer() {}

    public static String serializeToJson(Object object, Set<Object> visitedObjects) {
        if (object == null) return "null";
        if (visitedObjects.contains(object)) return "\"<circular reference>\"";
        visitedObjects.add(object);

        Class<?> clazz = object.getClass();

        System.out.println("\n\nclazz: " + clazz);
        System.out.println("\nobject: " + object);
        if (isPrimitiveOrWrapper(object)) {
            return serializePrimitive(object);
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

    public static boolean isPrimitiveOrWrapper(Object object) {
        return object instanceof String || object instanceof Number || object instanceof Boolean || object instanceof Character;
    }

    public static String serializePrimitive(Object object) {
        return "\"" + object.toString() + "\"";
    }

    public static String serializeArray(Object object, Set<Object> visitedObjects) {
        return "TODO ARRAY";
    }

    public static String serializeCollection(Collection<?> collection, Set<Object> visitedObjects) {
        return "TODO COLLECTION";
    }

    public static String serializeMap(Map<?, ?> map, Set<Object> visitedObjects) {
        return "TODO MAP";
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
