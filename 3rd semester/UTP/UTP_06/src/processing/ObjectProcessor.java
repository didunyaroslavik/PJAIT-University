package processing;

public class ObjectProcessor {

    public String process(Object obj) {
        try {
            System.out.println("Starting validation...");
            Validator.validate(obj);
            System.out.println("Validation successful.");

            String json = JsonSerializer.serialize(obj);
            System.out.println("Serialization successful.");
            return json;
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        } catch (IllegalAccessException e) {
            return "{\"status\":\"error\",\"message\":\"Internal server error\"}";
        }
    }
}
