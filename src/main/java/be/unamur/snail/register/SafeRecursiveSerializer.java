package be.unamur.snail.register;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class SafeRecursiveSerializer extends JsonSerializer<Object> {
    private final Set<Integer> visited = new HashSet<>();
    private final int maxDepth;

    public SafeRecursiveSerializer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        serialize(o, jsonGenerator, 0);
    }

    private void serialize(Object obj, JsonGenerator gen, int depth) throws IOException {
        if (obj == null) {
            gen.writeNull();
            return;
        }

        Class<?> clazz = obj.getClass();
        int identity = System.identityHashCode(obj);
        if (visited.contains(identity)) {
            gen.writeStartObject();
            gen.writeStringField("_ref", "@" + identity);
            gen.writeEndObject();
            return;
        }

        if (obj instanceof Enum<?>) {
            gen.writeString(((Enum<?>) obj).name());
            return;
        }

        if (depth > maxDepth || isPrimitiveOrWrapper(clazz) || obj instanceof String) {
            writePrimitive(gen, obj);
            return;
        }

        visited.add(identity);

        if (clazz.isArray()) {
            writeArray(gen, obj, depth);
        }

        if (obj instanceof Collection) {
            writeCollection(gen, obj, depth);
        }

        if (obj instanceof Map) {
            writeMap(gen, obj, depth, identity);
        }

        gen.writeStartObject();
        gen.writeStringField("_class", clazz.getName());
        gen.writeStringField("_id", "@" + identity);

        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(obj);
                gen.writeFieldName(field.getName());
                serialize(fieldValue, gen, depth + 1);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field " + field.getName(), e);
            }
        }
        gen.writeEndObject();
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

    private void writePrimitive(JsonGenerator gen, Object obj) throws IOException {
        if (obj == null) {
            gen.writeNull();
        } else if (obj instanceof String) {
            gen.writeString((String) obj);
        } else if (obj instanceof Number) {
            if (obj instanceof Integer) {
                gen.writeNumber((Integer) obj);
            } else if (obj instanceof Long) {
                gen.writeNumber((Long) obj);
            } else if (obj instanceof Double) {
                gen.writeNumber((Double) obj);
            } else if (obj instanceof Float) {
                gen.writeNumber((Float) obj);
            } else if (obj instanceof Short) {
                gen.writeNumber((Short) obj);
            } else if (obj instanceof Byte) {
                gen.writeNumber((Byte) obj);
            } else {
                gen.writeNumber(((Number) obj).doubleValue());
            }
        } else if (obj instanceof Boolean) {
            gen.writeBoolean((Boolean) obj);
        } else if (obj instanceof Character) {
            gen.writeString(obj.toString());
        } else {
            // Fallback: use string representation
            gen.writeString(obj.toString());
        }
    }

    private void writeArray(JsonGenerator gen, Object obj, int depth) throws IOException {
        gen.writeStartArray();
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(obj, i);
            serialize(element, gen, depth + 1);
        }
        gen.writeEndArray();
    }

    private void writeCollection(JsonGenerator gen, Object obj, int depth) throws IOException {
        gen.writeStartArray();
        for (Object element : (Collection<?>) obj) {
            serialize(element, gen, depth + 1);
        }
        gen.writeEndArray();
    }

    private void writeMap(JsonGenerator gen, Object obj, int depth, int identity) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("_class", obj.getClass().getName());
        gen.writeStringField("_id", "@" + identity);
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
            gen.writeFieldName(String.valueOf(entry.getKey()));
            serialize(entry.getValue(), gen, depth + 1);
        }
        gen.writeEndObject();

    }
}
