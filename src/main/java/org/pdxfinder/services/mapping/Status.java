package org.pdxfinder.services.mapping;


public enum Status {

    unmapped,
    created,
    orphaned,
    validated,
    unvalidated;

    public String get() {
        return name();
    }
}
