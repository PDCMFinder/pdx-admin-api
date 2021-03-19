package org.pdxfinder.constants;


import java.util.HashMap;
import java.util.Map;

public enum MappingEntityType {

    DIAGNOSIS("diagnosis") ,
    TREATMENT("treatment");

    private static final Map<String, MappingEntityType> BY_LABEL = new HashMap<>();
    static
    {
        for (MappingEntityType e: values())
        {
            BY_LABEL.put(e.label, e);
        }
    }

    private final String label;

    MappingEntityType(String label)
    {
        this.label = label;
    }


    public static MappingEntityType valueOfLabel(String label)
    {
        return BY_LABEL.get(label);
    }

    public String getLabel()
    {
        return label;
    }
}
