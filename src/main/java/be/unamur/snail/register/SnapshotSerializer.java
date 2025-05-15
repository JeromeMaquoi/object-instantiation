package be.unamur.snail.register;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnapshotSerializer {
    private static final Logger log = LoggerFactory.getLogger(SnapshotSerializer.class);
    private final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    private final int maxDepth;
    private final ObjectMapper mapper;

    public SnapshotSerializer(int maxDepth) {
        this.maxDepth = maxDepth;
        this.mapper = new ObjectMapper();
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
    }

    public String serializeToJson(Object root) {
        System.out.println("\nobject annotation : " + root.getClass().getAnnotation(JsonIdentityInfo.class));

//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        /*mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);*/

        Object snapshot = buildSnapshot(root, 0);
        try {
            return mapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Object buildSnapshot(Object obj, int depth) {
        if (obj == null) return null;
        if (visited.contains(obj)) return Map.of("ref", identity(obj), "note", "already visited");
        if (depth > maxDepth || isPrimitiveOrWrapper(obj.getClass()) || obj instanceof String) return obj;

        visited.add(obj);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("_class", obj.getClass().getName());
        result.put("_id", identity(obj));

        for (Field field : getAllFields(obj.getClass())) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(obj);
                result.put(field.getName(), buildSnapshot(fieldValue, depth + 1));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(result);
        return result;
    }

    private String identity(Object obj) {
        return "@" + System.identityHashCode(obj);
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class || clazz == Byte.class ||
                clazz == Character.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class;
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }







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
            if (item == object && ! (object instanceof Boolean) && ! (object instanceof String)) {
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
//        Pattern pattern = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
//        Matcher matcher = pattern.matcher(object.toString());
//        StringBuilder builder = new StringBuilder();
//        while (matcher.find()) {
//            String unicodeSequence = matcher.group();
//            char unicodeChar = (char) Integer.parseInt(unicodeSequence.substring(2), 16);
//            matcher.appendReplacement(builder, Character.toString(unicodeChar));
//        }
//        matcher.appendTail(builder);
//        return builder.toString();

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
