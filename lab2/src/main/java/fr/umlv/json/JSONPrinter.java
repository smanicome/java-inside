package fr.umlv.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONPrinter {
    /*public static String toJSON(fr.umlv.json.Person person) {
        return """
      {
        "firstName": "%s",
        "lastName": "%s"
      }
      """.formatted(person.firstName(), person.lastName());
    }

    public static String toJSON(fr.umlv.json.Alien alien) {
        return """
      {
        "age": %s,
        "planet": "%s"
      }
      """.formatted(alien.age(), alien.planet());
    }*/

    public static String toJSON(Record record) {
        Objects.requireNonNull(record);

        Stream<RecordComponent> stream = Arrays.stream(record.getClass().getRecordComponents());
        return stream.map(elt -> invokeAccessor(record, elt))
            .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }

    private static String invokeAccessor(Record record, RecordComponent recordComponent) {
        try {
            String jsonLine = "  \"";

            if(recordComponent.isAnnotationPresent(JSONProperty.class)) {
                jsonLine += recordComponent.getAnnotation(JSONProperty.class).value();
            } else {
                jsonLine += recordComponent.getName();
            }

            jsonLine += "\":";

            Object value = recordComponent.getAccessor().invoke(record);
            if(recordComponent.getAccessor().invoke(record) instanceof String) {
                jsonLine += "\"";
                jsonLine += value.toString();
                jsonLine += "\"";
            } else {
                jsonLine += recordComponent.getAccessor().invoke(record).toString();
            }

            return jsonLine;

        } catch (IllegalAccessException e) {
            throw new IllegalAccessError();
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if(cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if(cause instanceof Error error) {
                throw error;
            }

            throw new UndeclaredThrowableException(cause);
        }
    }

    public static void main(String[] args) {
        var person = new Person("John", "Doe");
        System.out.println(toJSON(person));
        var alien = new Alien(100, "Saturn");
        System.out.println(toJSON(alien));
    }
}