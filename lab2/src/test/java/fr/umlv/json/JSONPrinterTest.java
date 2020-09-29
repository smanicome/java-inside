package fr.umlv.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSONPrinterTest {

    @Test
    void person() {
        var person = new Person("John", "Doe");
        assertEquals("""
                {
                  "firstName":"John",
                  "lastName":"Doe"
                }""", JSONPrinter.toJSON(person));
    }

    @Test
    void alien() {
        var alien = new Alien(100, "Saturn");
        assertEquals("""
                {
                  "age":100,
                  "planet":"Saturn"
                }""", JSONPrinter.toJSON(alien));
    }

    @Test
    void parse() {
        assertThrows(IllegalStateException.class, () -> IncompleteJSONParser.parse("12243"));
    }

    @Test
    void parsePerson() {
        var person = new Person("John", "Doe");
        assertDoesNotThrow(() -> IncompleteJSONParser.parse(JSONPrinter.toJSON(person)));
    }

    @Test
    void parseAlien() {
        var alien = new Alien(100, "Saturn");
        assertDoesNotThrow(() -> IncompleteJSONParser.parse(JSONPrinter.toJSON(alien)));
    }
}