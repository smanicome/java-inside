package fr.umlv.json;

import static java.util.Objects.requireNonNull;

public record Person(@JSONProperty String first_name, @JSONProperty String last_name) {
    public Person {
        requireNonNull(first_name);
        requireNonNull(last_name);
    }
}