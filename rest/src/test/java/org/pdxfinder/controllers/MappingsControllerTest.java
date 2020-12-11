package org.pdxfinder.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pdxfinder.CSVHandler;
import org.pdxfinder.MappingContainer;
import org.pdxfinder.MappingEntity;
import org.pdxfinder.MappingService;
import org.pdxfinder.MissingMappingService;
import org.pdxfinder.UtilityService;
import org.pdxfinder.dto.PaginationDTO;
import org.pdxfinder.util.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@WebMvcTest(MappingsController.class)
class MappingsControllerTest {

  private static final int PAGE = 1;
  private static final int SIZE = 10;
  private static final int TOTAL_ELEMENTS = 1;
  private static final String ENTITY_TYPE = "ENTITY_TYPE";
  private static final List<String> ENTITY_TYPE_LIST = Collections.singletonList(ENTITY_TYPE);
  private static final String MAPPING_LABEL = "MAPPING_LABEL";
  private static final List<String> MAPPING_LABEL_LIST = Collections.singletonList(MAPPING_LABEL);
  private static final String MAPPING_VALUE = "MAPPING_VALUE";
  private static final List<String> MAPPING_VALUE_LIST = Collections.singletonList(MAPPING_VALUE);
  private static final Map<String, String> MAPPING_VALUE_MAP = new HashMap<>();
  private static final String MAPPED_TERM_LABEL = "MAPPED_TERM_LABEL";
  private static final String MAPPED_TERM_URL = "MAPPED_TERM_URL";
  private static final String MAP_TYPE = "MAP_TYPE";
  private static final String MAPPED_TERMS_ONLY = "MAPPED_TERMS_ONLY";
  private static final String STATUS = "STATUS";
  private static final List<String> STATUS_LIST = Collections.singletonList(STATUS);
  private static final long ENTITY_ID = 1;
  private static final String JUSTIFICATION = "JUSTIFICATION";
  private static final List<MappingEntity> SUGGESTED_MAPPINGS = new ArrayList<>();
  private static final String DATA_SOURCE = "DATA_SOURCE";
  private static final int UNMAPPED = 2;
  private static final int MAPPED = 26;
  private static final int VALIDATED = 3;
  private static final int CREATED = 1;
  private static final int ORPHANED = 0;

  private static final String BASE_URL = "/api/";
  private static final String MAPPINGS_URL = BASE_URL + "mappings/";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MappingService mappingService;

  @MockBean
  private MissingMappingService missingMappingService;

  @MockBean
  private RestTemplateBuilder restTemplateBuilder;

  @MockBean
  private CSVHandler csvHandler;

  @MockBean
  private UtilityService utilityService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testGetMappings() throws Exception {
    PaginationDTO paginationDTO = new PaginationDTO();
    paginationDTO.setPage(PAGE);
    paginationDTO.setSize(SIZE);
    paginationDTO.setTotalElements(TOTAL_ELEMENTS);
    Object data = "data";
    paginationDTO.setData(data);

    String url = BASE_URL + "mappings?mq=" + MAPPING_LABEL + ":" + MAPPING_VALUE
        + "&entity-type=" + ENTITY_TYPE
        + "&mapped-term=" + MAPPED_TERM_LABEL
        + "&map-terms-only=" + MAPPED_TERMS_ONLY
        + "&map-type=" + MAP_TYPE
        + "&status=" + String.join(",", STATUS_LIST)
        + "&page=" + PAGE + "&size=" + SIZE;
    when(mappingService.search(
        PAGE,
        SIZE,
        ENTITY_TYPE_LIST,
        MAPPING_LABEL,
        MAPPING_VALUE_LIST,
        MAPPED_TERM_LABEL,
        MAP_TYPE,
        MAPPED_TERMS_ONLY,
        STATUS_LIST)).thenReturn(paginationDTO);

    this.mockMvc.perform(get(url))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size", is(10)))
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.page", is(1)))
        .andExpect(jsonPath(".data", is(Arrays.asList(data))));
  }

  @Test
  public void testGetOneMapping() throws Exception {
    MappingEntity mappingEntity = new MappingEntity();
    mappingEntity.setEntityId(ENTITY_ID);
    mappingEntity.setEntityType(ENTITY_TYPE);
    mappingEntity.setMappingLabels(MAPPING_LABEL_LIST);
    mappingEntity.setMappingValues(MAPPING_VALUE_MAP);
    mappingEntity.setMappedTermLabel(MAPPED_TERM_LABEL);
    mappingEntity.setMappedTermUrl(MAPPED_TERM_URL);
    mappingEntity.setMapType(MAP_TYPE);
    mappingEntity.setJustification(JUSTIFICATION);
    mappingEntity.setStatus(STATUS);
    mappingEntity.setSuggestedMappings(SUGGESTED_MAPPINGS);

    String url = MAPPINGS_URL + ENTITY_ID;
    System.out.println(url);
    when(mappingService.getMappingEntityById((int) ENTITY_ID)).thenReturn(mappingEntity);

    this.mockMvc.perform(get(url))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entityId", is((int) ENTITY_ID)))
        .andExpect(jsonPath("$.entityType", is(ENTITY_TYPE)))
        .andExpect(jsonPath("$.mappingLabels", is(MAPPING_LABEL_LIST)))
        .andExpect(jsonPath("$.mappingValues", is(MAPPING_VALUE_MAP)))
        .andExpect(jsonPath("$.mappedTermLabel", is(MAPPED_TERM_LABEL)))
        .andExpect(jsonPath("$.mappedTermUrl", is(MAPPED_TERM_URL)))
        .andExpect(jsonPath("$.mapType", is(MAP_TYPE)))
        .andExpect(jsonPath("$.justification", is(JUSTIFICATION)))
        .andExpect(jsonPath("$.status", is(STATUS)))
        .andExpect(jsonPath("$.suggestedMappings", is(SUGGESTED_MAPPINGS)));
  }

  @Test
  public void testGetMappingStatSummary() throws Exception {
    String url = MAPPINGS_URL + "summary?entity-type=" + ENTITY_TYPE;
    List<Map> mappingSummary = getMappingSummary();
    System.out.println(url);
    when(mappingService.getMappingSummary(ENTITY_TYPE)).thenReturn(mappingSummary);

    this.mockMvc.perform(get(url))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].DataSource", is(DATA_SOURCE)))
        .andExpect(jsonPath("$[0].Unmapped", is(UNMAPPED)))
        .andExpect(jsonPath("$[0].Mapped", is(MAPPED)))
        .andExpect(jsonPath("$[0].Validated", is(VALIDATED)))
        .andExpect(jsonPath("$[0].Created", is(CREATED)))
        .andExpect(jsonPath("$[0].Orphaned", is(ORPHANED)));
  }

  private List<Map> getMappingSummary() {
    List<Map> mappingSummary = new ArrayList<>();
    Map summary = new HashMap();
    summary.put("DataSource", DATA_SOURCE);
    summary.put("Unmapped", UNMAPPED);
    summary.put("Mapped", MAPPED);
    summary.put("Validated", VALIDATED);
    summary.put("Created", CREATED);
    summary.put("Orphaned", ORPHANED);
    mappingSummary.add(summary);
    return mappingSummary;
  }

  @Test
  public void testGetMissingMappings() throws Exception {
    MappingContainer mappingContainer = getMappingContainer();;
    String url = MAPPINGS_URL + "getmissingmappings";
    when(missingMappingService.getMissingMappings()).thenReturn(mappingContainer);

    this.mockMvc.perform(get(url))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].entityId", is((int) ENTITY_ID)))
        .andExpect(jsonPath("$[0].entityType", is(ENTITY_TYPE)))
        .andExpect(jsonPath("$[0].mappingLabels", is(MAPPING_LABEL_LIST)))
        .andExpect(jsonPath("$[0].mappingValues", is(MAPPING_VALUE_MAP)))
        .andExpect(jsonPath("$[0].mappedTermLabel", is(MAPPED_TERM_LABEL)))
        .andExpect(jsonPath("$[0].mappedTermUrl", is(MAPPED_TERM_URL)))
        .andExpect(jsonPath("$[0].mapType", is(MAP_TYPE)))
        .andExpect(jsonPath("$[0].justification", is(JUSTIFICATION)))
        .andExpect(jsonPath("$[0].status", is(STATUS)))
        .andExpect(jsonPath("$[0].suggestedMappings", is(SUGGESTED_MAPPINGS)));
  }

  @Test
  public void testEditListOfEntityMappings() throws Exception {
    String url = MAPPINGS_URL;
    List<MappingEntity> submittedEntities = new ArrayList<>();
    submittedEntities.add(getMappingEntity());
    List<MappingEntity> updated = new ArrayList<>();
    updated.add(getMappingEntity());
    when(mappingService.updateRecords(any())).thenReturn(updated);
    when(mappingService.checkExistence(ENTITY_ID)).thenReturn(true);

    this.mockMvc.perform(put(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonHelper.toJson(submittedEntities)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].entityId", is((int) ENTITY_ID)))
        .andExpect(jsonPath("$[0].entityType", is(ENTITY_TYPE)))
        .andExpect(jsonPath("$[0].mappingLabels", is(MAPPING_LABEL_LIST)))
        .andExpect(jsonPath("$[0].mappingValues", is(MAPPING_VALUE_MAP)))
        .andExpect(jsonPath("$[0].mappedTermLabel", is(MAPPED_TERM_LABEL)))
        .andExpect(jsonPath("$[0].mappedTermUrl", is(MAPPED_TERM_URL)))
        .andExpect(jsonPath("$[0].mapType", is(MAP_TYPE)))
        .andExpect(jsonPath("$[0].justification", is(JUSTIFICATION)))
        .andExpect(jsonPath("$[0].status", is(STATUS)))
        .andExpect(jsonPath("$[0].suggestedMappings", is(SUGGESTED_MAPPINGS)));
  }

  @Test
  public void testUploadData() throws Exception {
    String url = MAPPINGS_URL + "uploads?entity-type=diagnosis";
    String CSV_INPUT = "input_update.csv";
    this.getClass().getResourceAsStream(CSV_INPUT);
    MockMultipartFile file
        = new MockMultipartFile(
        "uploads",
        "input_update.csv",
        MediaType.TEXT_PLAIN_VALUE,
        this.getClass().getResourceAsStream(CSV_INPUT)
    );

    List<Map<String, String>> csvData = new ArrayList<>();
    List<MappingEntity> updatedData = new ManagedList<>();
    updatedData.add(getMappingEntity());
    when(utilityService.serializeMultipartFile(any())).thenReturn(csvData);
    when(csvHandler.validateUploadedCSV(csvData)).thenReturn(new ArrayList());
    when(mappingService.processUploadedCSV(csvData)).thenReturn(updatedData);

    this.mockMvc.perform(multipart(url)
        .file(file))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].entityId", is((int) ENTITY_ID)))
        .andExpect(jsonPath("$[0].entityType", is(ENTITY_TYPE)))
        .andExpect(jsonPath("$[0].mappingLabels", is(MAPPING_LABEL_LIST)))
        .andExpect(jsonPath("$[0].mappingValues", is(MAPPING_VALUE_MAP)))
        .andExpect(jsonPath("$[0].mappedTermLabel", is(MAPPED_TERM_LABEL)))
        .andExpect(jsonPath("$[0].mappedTermUrl", is(MAPPED_TERM_URL)))
        .andExpect(jsonPath("$[0].mapType", is(MAP_TYPE)))
        .andExpect(jsonPath("$[0].justification", is(JUSTIFICATION)))
        .andExpect(jsonPath("$[0].status", is(STATUS)))
        .andExpect(jsonPath("$[0].suggestedMappings", is(SUGGESTED_MAPPINGS)));
  }

  @Disabled
  @Test
  public void testExportMappingData() throws Exception {
    List<MappingEntity> mappingEntities = new ArrayList<>();
    mappingEntities.add(getMappingEntity());
    String url = MAPPINGS_URL + "export"
        + "?mq=" + MAPPING_LABEL + ":" + MAPPING_VALUE
        + "&entity-type=" + ENTITY_TYPE
        + "&mapped-term=" + MAPPED_TERM_LABEL
        + "&map-terms-only=" + MAPPED_TERMS_ONLY
        + "&map-type=" + MAP_TYPE
        + "&status=" + String.join(",", STATUS_LIST)
        + "&page=" + PAGE;
    PaginationDTO paginationDTO = new PaginationDTO();
    paginationDTO.setPage(PAGE);
    paginationDTO.setSize(SIZE);
    paginationDTO.setTotalElements(TOTAL_ELEMENTS);

    paginationDTO.setAdditionalProperty("mappings", mappingEntities);
    Object data = "data";
    paginationDTO.setData(data);

    when(mappingService.search(
        PAGE,
        30000,
        ENTITY_TYPE_LIST,
        MAPPING_LABEL,
        MAPPING_VALUE_LIST,
        MAPPED_TERM_LABEL,
        MAP_TYPE,
        MAPPED_TERMS_ONLY,
        STATUS_LIST)).thenReturn(paginationDTO);

    this.mockMvc.perform(get(url))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entityId", is((int) ENTITY_ID)))
        .andExpect(jsonPath("$.entityType", is(ENTITY_TYPE)))
        .andExpect(jsonPath("$.mappingLabels", is(MAPPING_LABEL_LIST)))
        .andExpect(jsonPath("$.mappingValues", is(MAPPING_VALUE_MAP)))
        .andExpect(jsonPath("$.mappedTermLabel", is(MAPPED_TERM_LABEL)))
        .andExpect(jsonPath("$.mappedTermUrl", is(MAPPED_TERM_URL)))
        .andExpect(jsonPath("$.mapType", is(MAP_TYPE)))
        .andExpect(jsonPath("$.justification", is(JUSTIFICATION)))
        .andExpect(jsonPath("$.status", is(STATUS)))
        .andExpect(jsonPath("$.suggestedMappings", is(SUGGESTED_MAPPINGS)));
  }

  private MappingContainer getMappingContainer() {
    MappingContainer mappingContainer = new MappingContainer();
    TreeMap<String, MappingEntity> mappings = new TreeMap<>();
    mappings.put("key", getMappingEntity());
    mappingContainer.setMappings(mappings);
    return mappingContainer;
  }

  private MappingEntity getMappingEntity() {
    MappingEntity mappingEntity = new MappingEntity();
    mappingEntity.setEntityId(ENTITY_ID);
    mappingEntity.setEntityType(ENTITY_TYPE);
    mappingEntity.setMappingLabels(MAPPING_LABEL_LIST);
    mappingEntity.setMappingValues(MAPPING_VALUE_MAP);
    mappingEntity.setMappedTermLabel(MAPPED_TERM_LABEL);
    mappingEntity.setMappedTermUrl(MAPPED_TERM_URL);
    mappingEntity.setMapType(MAP_TYPE);
    mappingEntity.setJustification(JUSTIFICATION);
    mappingEntity.setStatus(STATUS);
    mappingEntity.setSuggestedMappings(SUGGESTED_MAPPINGS);
    return mappingEntity;
  }

}