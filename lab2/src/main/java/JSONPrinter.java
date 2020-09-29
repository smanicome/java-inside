import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONPrinter {
    /*public static String toJSON(Person person) {
        return """
      {
        "firstName": "%s",
        "lastName": "%s"
      }
      """.formatted(person.firstName(), person.lastName());
    }

    public static String toJSON(Alien alien) {
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
            .collect(Collectors.joining(","));
    }

    private static String invokeAccessor(Record record, RecordComponent recordComponent) {
        try {
            return recordComponent.getAccessor().invoke(record).toString();
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        var person = new Person("John", "Doe");
        System.out.println(toJSON(person));
        var alien = new Alien(100, "Saturn");
        System.out.println(toJSON(alien));
    }
}