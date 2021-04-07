package org.pdxfinder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@RequiredArgsConstructor
public class MappingEntity {

    /**
     * A Long number identifying the entity. This id is used for referring the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entityId;

    /**
     * Describes what kind of mapping info is held in the entity, ie: diagnosis, drug, etc
     */
    @EqualsAndHashCode.Include
    private String entityType;


    /**
     * A list of attributes that are used to map objects.
     * IE: ["diagnosis", "source", "primaryTissue", "tumorType"]
     * The elements of this list is used as headers when listing the mapping entities as well as keys to the mappingValues
     */
    @EqualsAndHashCode.Include
    @ElementCollection
    @CollectionTable(name="mapping_labels", joinColumns = @JoinColumn(name = "mapping_entity_id"))
    @Column(name="mapping_labels")
    private List<String> mappingLabels;


    /**
     * The corresponding values for the mapping labels
     * IE: ["diagnosis"=>"Carcinoma", "source"=>"JAX"]
     */
    @EqualsAndHashCode.Include
    @ElementCollection
    @CollectionTable(name="mapping_values", joinColumns = @JoinColumn(name = "mapping_entity_id"))
    @Column(name="mapping_values", columnDefinition="Text")
    private Map<String, String> mappingValues;


    /**
     * The term that the entity is mapped to. The value of this attribute should be either null (indicating that the
     * entity is not mapped to anything yet) or an existing ontology term.
     */
    @EqualsAndHashCode.Include
    private String mappedTermLabel;




    /**
     * The term url that the entity is mapped to. The value of this attribute should be either null (indicating that the
     * entity is not mapped to anything yet) or an existing ontology term url.
     */
    @EqualsAndHashCode.Include
    private String mappedTermUrl;

    /**
     * Describes whether the mapping rule is direct or inferred
     */
    @EqualsAndHashCode.Include
    private String mapType;


    /**
     * Gives info about the justification: ie. manual curation, combination of diagnosis and primary tumor, etc
     */
    @EqualsAndHashCode.Include
    private String justification;

    /**
     * Possible values:
     * Created : temporary status when the entity is mapped to a term but editing is possible
     * Mapped : the entity is mapped to a term but the mapping is not yet verified by the institute
     * Verified : the entity is mapped to a term and the mapping is verified by the institute
     */
    @EqualsAndHashCode.Include
    private String status;


    /**
     * A list of entities that are similar to the current entity. This list is empty if the entity's mappedTermLabel is not null.
     */
    @EqualsAndHashCode.Include
    @ElementCollection
    @CollectionTable(name="mapping_suggest", joinColumns = @JoinColumn(name = "mapping_entity_id"))
    @Column(name="mapping_values")
    private List<MappingEntity> suggestedMappings;


    /**
     * The date when the entity was created.
     */
    private Date dateCreated;


    /**
     * The date when the entity was last updated.
     */
    private Date dateUpdated;


    /**
     * The unique String that identifies a Mapping
     */
    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String mappingKey;

    public MappingEntity(String entityType, List<String> mappingLabels, Map<String, String> mappingValues) {

        this.entityType = entityType;
        this.mappingLabels = mappingLabels;
        this.mappingValues = mappingValues;
        this.mappedTermLabel = null;
        this.status = "Created";
        this.suggestedMappings = new ArrayList<>();
        //TODO: get current date, specify date format
        this.dateCreated = null;
        this.dateUpdated = null;

    }

    public String generateMappingKey(){

        String key = entityType;

        for(String label : mappingLabels){

            key += "__" + mappingValues.get(label);
        }

        key = key.replaceAll("[^a-zA-Z0-9 _-]","");
        key = DigestUtils.sha256Hex(key);
        return key.toLowerCase();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n {");
        sb.append("\"entityId\":" + entityId + ", \n");
        sb.append("\"entityType\": \"" + entityType + "\", \n");
        sb.append("\"mappingLabels\":");

        sb.append(mappingLabels.stream()
                .map(n -> "\""+n+"\"")
                .collect(Collectors.joining(",", "[", "]")));
        sb.append(", \n");

        sb.append("\"mappingValues\":");
        sb.append(mappingLabels.stream()
                .map(n -> "\""+n+"\":\"" +mappingValues.get(n)+"\"")
                .collect(Collectors.joining(",", "{", "}")));
        sb.append(", \n");
        sb.append("\"mappedTermLabel\": \"" + mappedTermLabel + "\", \n");
        sb.append("\"mappedTermUrl\": \"" + mappedTermUrl + "\", \n");
        sb.append("\"mapType\": \"" + mapType + "\", \n");
        sb.append("\"justification\": \"" + justification + "\", \n");
        sb.append("\"status\": \"" + status + "\", \n");
        sb.append("\"suggestedMappings\": " + suggestedMappings + " \n");

        sb.append("}");

        return sb.toString();
    }
}
