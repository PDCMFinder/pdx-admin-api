package org.pdxfinder.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.util.IOUtils;
import org.pdxfinder.*;
import org.pdxfinder.constants.MappingEntityType;
import org.pdxfinder.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/mappings")
public class MappingsController {

  private static final Logger log = LoggerFactory.getLogger(MappingsController.class);
  private final ObjectMapper mapper = new ObjectMapper();
  private final UtilityService utilityService;
  private final MappingService mappingService;
  private final MissingMappingService missingMappingService;
  private final OntologyTermService ontologyTermService;
  private final CSVHandler csvHandler;

  private static final String MAPPING_VALUE_SEPARATOR = ":";

  @Autowired
  public MappingsController(
      MappingService mappingService,
      MissingMappingService missingMappingService,
      UtilityService utilityService,
      OntologyTermService ontologyTermService,
      CSVHandler csvHandler) {
    this.utilityService = utilityService;
    this.csvHandler = csvHandler;
    this.mappingService = mappingService;
    this.missingMappingService = missingMappingService;
    this.ontologyTermService = ontologyTermService;
  }

  /**
   * Provides entry point to query the MappingEntity data store E.g :
   * .../api/mappings?map-terms-only=true&mq=datasource:jax&entity-type=treatment
   *
   * @param mappingQuery    - Key value map of mappingValues e.g to filter by DataSource:jax,
   *                        ...?mq=datasource:jax
   * @param mappedTermLabel - Filters the data for missing mappings e.g To find missing mappings,
   *                        ...?mapped-term=-
   * @param entityType      - Search by entityType e.g find unmapped treatment entities
   *                        ...?entity-type=treatment&mapped-term=-
   * @param mappedTermsOnly - Search for mapped terms only ... map-terms-only=true
   * @param mapType         - Search data by mapType e.g ...?map-type=direct
   * @param status          - Search data by mapping status e.g ...?status=unmapped
   * @param page            - Allows client to submit offset value e.g ...?page=10
   * @param size            - Allows client to submit size limit values e.g ...?size=5
   * @return - Mapping Entities with data count, offset and limit Values
   */
  @GetMapping
  public ResponseEntity<?> getMappings(
      @RequestParam(value = "mq", defaultValue = "") String mappingQuery,
      @RequestParam(value = "mapped-term", defaultValue = "") String mappedTermLabel,
      @RequestParam(value = "map-terms-only", defaultValue = "") String mappedTermsOnly,
      @RequestParam(value = "entity-type", defaultValue = "0") List<String> entityType,
      @RequestParam(value = "map-type", defaultValue = "") String mapType,
      @RequestParam(value = "status", defaultValue = "0") List<String> status,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "size", defaultValue = "10") Integer size) {

    Pair<String, List<String>> mappingLabelAndValue = getMappingLabelAndValue(mappingQuery);
    String mappingLabel = mappingLabelAndValue.getKey();
    List<String> mappingValue = mappingLabelAndValue.getValue();

    PaginationDTO result = mappingService.search(
        page,
        size,
        entityType,
        mappingLabel,
        mappingValue,
        mappedTermLabel,
        mapType,
        mappedTermsOnly,
        status);

    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @GetMapping("{entityId}")
  public ResponseEntity<?> getOneMapping(@PathVariable Optional<Integer> entityId) {

    if (entityId.isPresent()) {
      MappingEntity result = mappingService.getMappingEntityById(entityId.get());
      return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
    return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST.getReasonPhrase(),
        HttpStatus.BAD_REQUEST);
  }

  @GetMapping("summary")
  public ResponseEntity<?> getMappingStatSummary(
      @RequestParam(value = "entity-type", defaultValue = "") String entityType) {
    List<Map> result = mappingService.getMappingSummary(entityType);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @GetMapping("getmissingmappings")
  public ResponseEntity<?> getMissingMappings() {
    MappingContainer missingMappings = missingMappingService.getMissingMappings();
    return new ResponseEntity<>(missingMappings.getEntityList(), HttpStatus.OK);
  }

  @GetMapping("/ontologies")
  public Object getOntologies(@RequestParam(value = "type", defaultValue = "diagnosis") Optional<String> dataType){

    String entityType = dataType.get();
    return ontologyTermService.getTermsByType(entityType);
  }


  @PutMapping
  public ResponseEntity<?> editListOfEntityMappings(
      @RequestBody List<MappingEntity> submittedEntities) {

    List data = mapper.convertValue(submittedEntities, List.class);
    log.info(data.toString());
    List<Error> errors = validateEntities(submittedEntities);
    if (submittedEntities.size() < 1 || !errors.isEmpty()) {
      return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
    List<MappingEntity> updated = mappingService.updateRecords(submittedEntities);
    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @PostMapping("uploads")
  public ResponseEntity<?> uploadData(
      @RequestParam("uploads") Optional<MultipartFile> uploads) {

    Object responseBody = "";
    HttpStatus responseStatus = HttpStatus.OK;

    if (uploads.isPresent()) {
      List<Map<String, String>> csvData = utilityService.serializeMultipartFile(uploads.get());
      List report = new ArrayList();
      try {
        report = csvHandler.validateUploadedCSV(csvData);
      } catch (Exception e) {
        report.add(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
      }

      if (report.isEmpty()) {
        List<MappingEntity> updatedData = mappingService.processUploadedCSV(csvData);
        responseBody = updatedData;
        responseStatus = HttpStatus.OK;
      } else {
        responseBody = report;
        responseStatus = HttpStatus.UNPROCESSABLE_ENTITY;
      }

    }

    return new ResponseEntity<>(responseBody, responseStatus);
  }

  @GetMapping("export")
  @ResponseBody
  public Object exportMappingData(
      HttpServletResponse response,
      @RequestParam(value = "mq", defaultValue = "") String mappingQuery,
      @RequestParam(value = "mapped-term", defaultValue = "") String mappedTermLabel,
      @RequestParam(value = "map-terms-only", defaultValue = "") String mappedTermsOnly,
      @RequestParam(value = "entity-type", defaultValue = "0") List<String> entityType,
      @RequestParam(value = "map-type", defaultValue = "") String mapType,
      @RequestParam(value = "status", defaultValue = "0") List<String> status,
      @RequestParam(value = "page", defaultValue = "1") Integer page) {

    Pair<String, List<String>> mappingLabelAndValue = getMappingLabelAndValue(mappingQuery);
    String mappingLabel = mappingLabelAndValue.getKey();
    List<String> mappingValue = mappingLabelAndValue.getValue();

    int size = 30000;
    PaginationDTO result = mappingService.search(
        page,
        size,
        entityType,
        mappingLabel,
        mappingValue,
        mappedTermLabel,
        mapType,
        mappedTermsOnly,
        status);

    List<MappingEntity> mappingEntities =
        (List<MappingEntity>) result.getAdditionalProperties().get("mappings");
    /*
     *  Get Mapping Entity CSV Header
     */
    MappingEntity me = mappingEntities.get(0);
    List<String> csvHead = csvHandler.getMappingEntityCSVHead(me);

    /*
     *  Get Mapping Entity CSV Data Body
     */
    List<List<String>> mappingDataCSV = csvHandler.prepareMappingEntityForCSV(mappingEntities);

    CsvMapper mapper = new CsvMapper();
    CsvSchema.Builder builder = CsvSchema.builder();

    for (String head : csvHead) {
      builder.addColumn(head);
    }
    CsvSchema schema = builder.build().withHeader();

    String csvReport = "CSV Report";
    try {
      csvReport = mapper.writer(schema).writeValueAsString(mappingDataCSV);
    } catch (JsonProcessingException e) {
    }

    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition",
        "attachment; filename=pdxAdmin-" + me.getStatus() + ".csv");
    try {
      response.getOutputStream().flush();
    } catch (Exception e) {

    }
    return csvReport;
  }

  public List<Error> validateEntities(List<MappingEntity> mappingEntities) {
    List<Error> errors = new ArrayList<>();
    for (MappingEntity me : mappingEntities) {
      if (!mappingService.checkExistence(me.getEntityId())) {
        Error error = new Error("Entity " + me.getEntityId() + " Not Found", HttpStatus.NOT_FOUND);
        errors.add(error);
      }
    }
    return errors;
  }

  Pair<String, List<String>> getMappingLabelAndValue(String query) {
    String mappingLabel = "";
    List<String> mappingValue = Arrays.asList("0");
    
    if (query.indexOf(MAPPING_VALUE_SEPARATOR) > 0) {
      String[] elements = query.split(MAPPING_VALUE_SEPARATOR);
      if (elements.length == 2) {
        mappingLabel = elements[0].trim();
        mappingValue = Arrays.asList(elements[1].trim());
      }
    }
    return Pair.of(mappingLabel, mappingValue);
  }

  @RequestMapping(value="/mappingRules", produces="application/zip")
  public void getZipOfMappingRules(HttpServletResponse response) throws IOException {

    //setting headers
    response.setStatus(HttpServletResponse.SC_OK);
    String fileName = "mappingRules_" + new SimpleDateFormat("yyyyMMddHHmm'.zip'").format(new Date());
    response.addHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");

    ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

    Map<String, String> mappingRulesPaths = mappingService.getMappingRulesPaths();
    // create a list to add files to be zipped
    ArrayList<File> files = new ArrayList<>(mappingRulesPaths.size());
    for (Map.Entry<String, String> entry : mappingRulesPaths.entrySet()) {
      System.out.println(entry.getKey() + ":" + entry.getValue());
      File mappingsFile = new File(entry.getValue());
      files.add(mappingsFile);
    }

    // package files
    for (File file : files) {
      //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
      zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
      FileInputStream fileInputStream = new FileInputStream(file);

      IOUtils.copy(fileInputStream, zipOutputStream);

      fileInputStream.close();
      zipOutputStream.closeEntry();
    }

    zipOutputStream.close();

  }

  @PostMapping("rebuildMappingEntities")
  public void rebuildMappingEntitiesInDataBase() {
    mappingService.rebuildDatabaseFromRulesFiles();
  }

  @PostMapping("rebuildRulesJsonFromDatabase")
  public void rebuildRulesJsonFromDatabase() {
    mappingService.writeMappingsToFile(MappingEntityType.DIAGNOSIS.getLabel());
    mappingService.writeMappingsToFile(MappingEntityType.TREATMENT.getLabel());
  }

}
