import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONPrinterTest {
    @Test
    void person() {
        var person = new Person("John", "Doe");
        assertEquals("John,Doe", JSONPrinter.toJSON(person));
    }

    @Test
    void alien() {
        var alien = new Alien(100, "Saturn");
        assertEquals("100,Saturn", JSONPrinter.toJSON(alien));
    }
}