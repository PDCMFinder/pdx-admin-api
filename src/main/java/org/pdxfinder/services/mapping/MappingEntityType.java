package org.pdxfinder.services.mapping;


public enum MappingEntityType {

    diagnosis,
    treatment;

    public String get() {
        return name();
    }
}
