package fr.umlv.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class JSONPrinter {
    private static final ClassValue<RecordComponent[]> recordComponentsCacher = new ClassValue<RecordComponent[]>() {
        @Override
        protected RecordComponent[] computeValue(Class<?> type) {
            return type.getRecordComponents();
        }
    };

    public record Person(@JSONProperty String first_name, @JSONProperty String last_name) {
        public Person {
            requireNonNull(first_name);
            requireNonNull(last_name);
        }
    }

    public record Alien(int age, String planet) {
        public Alien {
            if (age < 0) {
                throw new IllegalArgumentException("negative age");
            }
            requireNonNull(planet);
        }
    }

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

        Stream<RecordComponent> stream = Arrays.stream(recordComponentsCacher.get(record.getClass()));

        return stream.map(elt -> invokeAccessor(record, elt))
            .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }

    private static String invokeAccessor(Record record, RecordComponent recordComponent) {
        Objects.requireNonNull(record);
        Objects.requireNonNull(recordComponent);
        Object value;

        try {
            value = recordComponent.getAccessor().invoke(record);
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

        String jsonLine = "  \"";


        if(recordComponent.isAnnotationPresent(JSONProperty.class)) {
            jsonLine += recordComponent.getName().replace('_', '-');
        } else {
            jsonLine += recordComponent.getName();
        }

        jsonLine += "\":";

        if(value instanceof String) {
            jsonLine += "\"";
            jsonLine += value.toString();
            jsonLine += "\"";
        } else {
            jsonLine += value.toString();
        }

        return jsonLine;
    }

    public static void main(String[] args) {
        var person = new Person("John", "Doe");
        System.out.println(toJSON(person));
        var alien = new Alien(100, "Saturn");
        System.out.println(toJSON(alien));
    }
}