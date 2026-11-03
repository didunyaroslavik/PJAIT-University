package processing;

import json.JsonIgnore;
import json.JsonProperty;
import json.JsonSerializable;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class JsonSerializer {

    public static String serialize(Object obj) throws IllegalAccessException {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize null");
        }
        if (!obj.getClass().isAnnotationPresent(JsonSerializable.class)) {
            throw new IllegalArgumentException("Class " + obj.getClass().getSimpleName()
                    + " is not @JsonSerializable, serialization is not possible");
        }

        StringJoiner jsonElements = new StringJoiner(",");

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }

            String key;
            if (field.isAnnotationPresent(JsonProperty.class)) {
                key = field.getAnnotation(JsonProperty.class).value();
            } else {
                key = field.getName();
            }

            Object value = field.get(obj);
            String formattedValue;
            if (value instanceof String) {
                formattedValue = "\"" + value + "\"";
            } else {
                formattedValue = String.valueOf(value);
            }

            jsonElements.add("\"" + key + "\":" + formattedValue);
        }

        return "{" + jsonElements.toString() + "}";
    }
}
