package processing;

import validation.NotBlank;
import validation.NotNull;
import validation.Range;

import java.lang.reflect.Field;

public class Validator {

    public static void validate(Object obj) throws ValidationException, IllegalAccessException {
        if (obj == null) {
            throw new ValidationException("Object is null");
        }

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            String name = field.getName();

            if (field.isAnnotationPresent(NotNull.class)) {
                if (value == null) {
                    throw new ValidationException("Field '" + name + "' cannot be null");
                }
            }

            if (field.isAnnotationPresent(NotBlank.class)) {
                if (value == null || value.toString().isBlank()) {
                    throw new ValidationException("Field '" + name + "' cannot be blank");
                }
            }

            if (field.isAnnotationPresent(Range.class)) {
                Range range = field.getAnnotation(Range.class);
                if (!(value instanceof Number)) {
                    throw new ValidationException("Field '" + name + "' is not a number");
                }
                long number = ((Number) value).longValue();
                if (number < range.min() || number > range.max()) {
                    throw new ValidationException("Field '" + name + "' must be between "
                            + range.min() + " and " + range.max() + ", but was " + number);
                }
            }
        }
    }
}
