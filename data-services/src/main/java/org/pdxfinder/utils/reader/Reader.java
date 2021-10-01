package org.pdxfinder.utils.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import tech.tablesaw.api.Table;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Reader {

    private static final Logger log = LoggerFactory.getLogger(Reader.class);
    private static final List<String> allowedTreatmentData = Arrays.asList("treatment", "drug");

    public Map<String, Table> readAllTsvFilesIn(Path targetDirectory, PathMatcher filter) {
        HashMap<String, Table> tables = new HashMap<>();
        try (final Stream<Path> stream = Files.list(targetDirectory)) {
            stream
                    .filter(filter::matches)
                    .forEach(path -> tables.put(
                            path.getFileName().toString(),
                            TableUtilities.readTsvOrReturnEmpty(path.toFile()))
                    );
        } catch (IOException e) {
            log.error("There was an error reading the files", e);
        }
        return tables;
    }


    public Map<String, Table> readAllTreatmentFilesIn(Path targetDirectory, PathMatcher filter){

        HashMap<String, Optional<Path>> potentialTreatmentPaths = new HashMap<>();
        for (String s : allowedTreatmentData) { potentialTreatmentPaths.put(s, getSubDirectory(targetDirectory, s)); }

        Map<String, Path> availableTreatmentPaths = new HashMap<>();
        potentialTreatmentPaths.forEach((k, v) -> v.ifPresent(t -> availableTreatmentPaths.put(k, t)));

        Map<String, Table> treatmentTables = new HashMap<>();
        // Only runs once
        availableTreatmentPaths.forEach((k, v) -> treatmentTables.putAll(readAllTsvFilesIn(v, filter)));
        return treatmentTables;

    }

    public Map<String, String> readyamlfromfilesystem(Path targetDirectory, PathMatcher filter) {
        Map<String, String> yamlMap = Map.of("provider_abbreviation", "");
        String error = String.format("No source.yaml fond in directory %s", targetDirectory);
        try (final Stream<Path> stream = Files.walk(targetDirectory, 2)) {
            Path path = stream.filter(filter::matches)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(error));
            yamlMap = readYamlToMap(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yamlMap;
    }

    private Map<String, String> readYamlToMap(Path yamlFile) {
        Map<String, String> yamlMap = Map.of("provider_abbreviation", "");
        try {
            Yaml yaml = new Yaml();
            InputStream filestream = new FileInputStream(yamlFile.toString());
            yamlMap = yaml.load(filestream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yamlMap;
    }

    Optional<Path> getSubDirectory(Path targetDirectory, String subDirectoryName) {
        Optional omicsDirectory;
        return targetDirectory.resolve(subDirectoryName).toFile().exists() ?
                Optional.of(targetDirectory.resolve(subDirectoryName)) :
                Optional.empty();
    }

    public List<Path> getOmicFilePaths(Path targetDirectory) {
        List<Path> paths = new ArrayList<>();
        try(Stream<Path> walk = Files.walk(targetDirectory)) {
            PathMatcher omicPatterns = FileSystems.getDefault().getPathMatcher("glob:**/{cyto,mut,cna,expression}/**.tsv");
            return walk
                    .filter(omicPatterns::matches)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("There was an error listing the files for {}", targetDirectory, e);
        }
        return paths;
    }

    public Table readOmicTable(Path path) {
        return TableUtilities.readTsvOrReturnEmpty(path.toFile());
    }

    public String getOmicDataType(Path path) {
        if (path.toString().contains("/cyto/")) {
            return "cytogenetics";
        } else if (path.toString().contains("/mut/")) {
            return "mutation";
        } else if (path.toString().contains("/cna/")) {
            return "copy number alteration";
        } else if (path.toString().contains("/expression/")) {
            return "expression";
        } else {
            throw new IllegalArgumentException("No recognised omic data type in file path {}");
        }
    }

}