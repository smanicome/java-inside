package fr.umlv.json;

import static java.util.Objects.requireNonNull;

public record Person(@JSONProperty("Prénom") String firstName, @JSONProperty("Nom") String lastName) {
    public Person {
        requireNonNull(firstName);
        requireNonNull(lastName);
    }
}