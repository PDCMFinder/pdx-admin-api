package org.pdxfinder.constants;


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
