package be.unamur.snail.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.*;

public class SnapshotSerializer {
    private static final Logger log = LoggerFactory.getLogger(SnapshotSerializer.class);

    private SnapshotSerializer() {}

    public static String serializeToJson(Object object, Set<Object> visitedObjects) {
        if (object == null) return "null";
        if (containsReference(object, visitedObjects)) return "\"<circular reference>\"";
        visitedObjects.add(object);

        Class<?> clazz = object.getClass();

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
                return serializePOJO(object, visitedObjects);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean containsReference(Object object, Set<Object> visitedObjects) {
        boolean contains = false;
        for (Object item : visitedObjects) {
            if (item == object) {
                contains = true;
                break;
            }
        }
        return contains;
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
        String raw = object.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
        return "\"" + raw + "\"";
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
            sb.append("\"").append(entry.getKey()).append("\":").append(serializeToJson(entry.getValue(), visitedObjects));
            if (iterator.hasNext()) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public static String serializePOJO(Object object, Set<Object> visitedObjects) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder("{");
        List<Field> fields = getAllNonStaticFields(object);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                sb.append("\"").append(field.getName()).append("\": ");
                sb.append(serializeToJson(value, visitedObjects));
                sb.append(", ");
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InaccessibleObjectException ignored) {}
        }
        if (sb.toString().endsWith(", ")) sb.setLength(sb.length() - 2);
        sb.append("}");
        return sb.toString();
    }

    public static <T> List<Field> getAllNonStaticFields(T t) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = t.getClass();
        while (clazz != Object.class) {
            fields.addAll(getNonStaticFieldsFromOneClass(clazz));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static <T> List<Field> getNonStaticFieldsFromOneClass(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> nonStaticFields = new ArrayList<>();
        for (Field field : declaredFields) {
            if (!Modifier.isStatic(field.getModifiers())) nonStaticFields.add(field);
        }
        return nonStaticFields;
    }
}
