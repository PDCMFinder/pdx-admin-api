package org.pdxfinder;


import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
public class OntologyTerm {

    private Long id;

    private String url;

    private String label;

    private Set<String> synonyms;

    private String type;

    private String description;



    public OntologyTerm() {
    }

    public OntologyTerm(String url, String label, String type) {
        this.url = url;
        this.label = label;
        this.type = type;
        this.synonyms = new HashSet<>();
    }



}
