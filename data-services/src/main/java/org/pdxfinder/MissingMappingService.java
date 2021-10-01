package org.pdxfinder;


import org.pdxfinder.utils.reader.Reader;
import org.pdxfinder.utils.reader.TableSetCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MissingMappingService {

    Reader reader;
    TableSetCleaner tableSetCleaner;
    MappingService mappingService;

    @Value("${data-dir}")
    private String rootDir;

    private Logger log = LoggerFactory.getLogger(MissingMappingService.class);

    MappingContainer missingMappingsContainer;

    @Autowired
    public MissingMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
        reader = new Reader();
        tableSetCleaner = new TableSetCleaner();
        missingMappingsContainer = new MappingContainer();

    }


    public MappingContainer getMissingMappings() {

        missingMappingsContainer = new MappingContainer();
        log.info("Deleting not mapped entities");
        mappingService.deleteMappingEntities(mappingService.getNotMappedEntities());
        populateMissingMappingsContainer();

        return missingMappingsContainer;
    }

    public void populateMissingMappingsContainer() {
        List<Path> folders = getProviderDirs();
        PathMatcher providerYaml = FileSystems.getDefault().getPathMatcher("glob:**source.yaml");
        for (Path path : folders) {
            String dataSourceAbbreviation = getProviderNameFromYaml(path, providerYaml);
            populateDiagnosisEntities(path, dataSourceAbbreviation);
            populateTreatmentEntities(path);
        }
    }

    private void populateDiagnosisEntities(Path path, String dataSourceAbbreviation) {
        PathMatcher metadataFile = FileSystems.getDefault().getPathMatcher("glob:**{metadata-sample}.tsv");
        Map<String, Table> metaDataTemplate = getAndCleanTemplateData(path, metadataFile);
        log.info(path.toString());
        getDiagnosisAttributesFromTemplate(metaDataTemplate, dataSourceAbbreviation);
    }

    private void populateTreatmentEntities(Path path) {
        PathMatcher drugDataFile = FileSystems.getDefault().getPathMatcher("glob:**{drug,treatment}*.tsv");
        Map<String, Table> drugDataTemplate = getAndCleanTemplateData(path, drugDataFile);
        getTreatmentAttributesFromTemplate(drugDataTemplate, path.getFileName().toString());
    }

    private Map<String, Table> getAndCleanTemplateData(Path path, PathMatcher file) {
        Map<String, Table> template = reader.readAllTsvFilesIn(path, file);
        return tableSetCleaner.cleanPdxTables(template);
    }

    private String getProviderNameFromYaml(Path path, PathMatcher providerYaml) {
        Map<String, String> yamlMap = reader.readyamlfromfilesystem(path, providerYaml);
        String dataSourceAbbreviation = yamlMap.get("provider");
        if (dataSourceAbbreviation.isEmpty()) {
            String error = String.format("could not read provider abbreviation for source.yaml in %s", path.toString());
            throw new IllegalArgumentException(error);
        }
        return dataSourceAbbreviation;
    }

    public List<Path> getProviderDirs() {

        Path updogDirectory = Paths.get(
                rootDir,
                "/data/UPDOG");

        List<Path> subfolders;
        try (var files = Files.walk(updogDirectory, 1)) {
            subfolders = files
                    .filter(p -> Files.isDirectory(p) && !p.equals(updogDirectory))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error opening updog dir");
            return new ArrayList<>();
        }
        return subfolders;
    }

    public void getDiagnosisAttributesFromTemplate(Map<String, Table> tables, String dataSource) {
        try {
            Table sampleTable = tables.get("metadata-patient_sample.tsv");

            for (Row row : sampleTable) {

                String primarySiteName = row.getString("primary_site");
                String diagnosis = row.getString("diagnosis");
                String tumorTypeName = row.getString("tumour_type");

                MappingEntity mappingEntity = mappingService.getDiagnosisMapping(dataSource, diagnosis, primarySiteName, tumorTypeName);

                if (mappingEntity == null) {
                    MappingEntity newUnmappedEntity = mappingService.saveUnmappedDiagnosis(dataSource, diagnosis, primarySiteName, tumorTypeName);
                    if (newUnmappedEntity != null)
                        missingMappingsContainer.addEntity(newUnmappedEntity);
                }
            }
        }
        catch (Exception e){
            log.error("Exception while getting diagnosis data from provider.");
        }
    }



    public void getTreatmentAttributesFromTemplate(Map<String, Table> tables, String abbrev){

            Table drugTable = tables.get("drugdosing-Sheet1.tsv");
            Table treatmentTable = tables.get("patienttreatment.tsv");
            getTreatmentAttributesFromTemplate(drugTable, abbrev);
            getTreatmentAttributesFromTemplate(treatmentTable, abbrev);
    }

    private void getTreatmentAttributesFromTemplate(Table table, String abbrev){

        try {
            if(table == null) return;
            for (Row row : table) {

                String treatmentName = row.getString("treatment_name");
                String[] drugArray = treatmentName.split("\\+");

                for(String drug:drugArray){
                    MappingEntity mappingEntity = mappingService.getTreatmentMapping(abbrev, drug.trim());

                    if (mappingEntity == null) {
                        MappingEntity newUnmappedEntity = mappingService.saveUnmappedTreatment(abbrev, drug.trim());
                        if (newUnmappedEntity != null)
                            missingMappingsContainer.addEntity(newUnmappedEntity);
                    }
                }

            }
        }
        catch (Exception e){
            log.error("Exception while getting treatment data from provider");
        }
    }

}