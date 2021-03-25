package org.pdxfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.pdxfinder.MappingService.MAPPING_RULE_NOT_FOUND;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pdxfinder.constants.DiagnosisMappingLabels;
import org.pdxfinder.constants.MappingEntityType;
import org.pdxfinder.constants.TreatmentMappingLabels;
import org.pdxfinder.repositories.MappingEntityRepository;
import org.pdxfinder.util.MappingEntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MappingServiceTest {

  @MockBean
  private MappingEntityRepository mappingEntityRepository;
  @Autowired
  private UtilityService utilityService;
  @MockBean
  private PaginationService paginationService;

  private static final String JAX_DATA_SOURCE = "jax";
  private static final String DIAGNOSIS = "acute myeloid leukemia";
  private static final String ORIGIN_TISSUE = "blood";
  private static final String TUMOR_TYPE = "primary";

  private static final String IRCC_CRC_DATA_SOURCE = "ircc-crc";
  private static final String TREATMENT_NAME = "0.9% Solution of Sodium Chloride";

  @Value("${data-dir}")
  private String rootDir;

  @Autowired
  private MappingService testInstance;

  @BeforeEach
  void setUp() {
  }

  @Test
  public void givenDiagnosisMappingExistsWhenGetDiagnosisMappingThenReturnData() {
    MappingEntity mappingEntity =
        testInstance.getDiagnosisMapping(JAX_DATA_SOURCE, DIAGNOSIS, ORIGIN_TISSUE, TUMOR_TYPE);

    assertThat(mappingEntity, is(notNullValue()));
    assertThat(mappingEntity.getEntityType(), is(MappingEntityType.DIAGNOSIS.getLabel()));
    Map<String, String> mappingValues = mappingEntity.getMappingValues();
    assertThat(mappingValues, is(notNullValue()));
    assertThat(mappingValues.get(DiagnosisMappingLabels.DATA_SOURCE.getLabel()), is(JAX_DATA_SOURCE));
    assertThat(mappingValues.get(DiagnosisMappingLabels.ORIGIN_TISSUE.getLabel()), is(ORIGIN_TISSUE));
    assertThat(mappingValues.get(DiagnosisMappingLabels.TUMOR_TYPE.getLabel()), is(TUMOR_TYPE));
    assertThat(mappingValues.get(DiagnosisMappingLabels.SAMPLE_DIAGNOSIS.getLabel()), is(DIAGNOSIS));
  }

  @Test
  public void givenNonExistingDataSourceWhenGetDiagnosisMappingThenReturnNull() {
    MappingEntity mappingEntity =
        testInstance.getDiagnosisMapping(
            "Non_existing_datasource", DIAGNOSIS, ORIGIN_TISSUE, TUMOR_TYPE);

    assertThat(mappingEntity, is(nullValue()));
  }

  @Test
  public void givenNonExistingDiagnosisWhenGetDiagnosisMappingThenReturnNull() {
    MappingEntity mappingEntity =
        testInstance.getDiagnosisMapping(
            JAX_DATA_SOURCE, "Non_existing_diagnosis", ORIGIN_TISSUE, TUMOR_TYPE);

    assertThat(mappingEntity, is(nullValue()));
  }

  @Test
  public void givenNonExistingOriginTissueWhenGetDiagnosisMappingThenReturnNull() {
    MappingEntity mappingEntity =
        testInstance.getDiagnosisMapping(
            JAX_DATA_SOURCE, DIAGNOSIS, "Non_existing_origin_tissue", TUMOR_TYPE);

    assertThat(mappingEntity, is(nullValue()));
  }

  @Test
  public void givenNonExistingTumorTypeWhenGetDiagnosisMappingThenReturnNull() {
    MappingEntity mappingEntity =
        testInstance.getDiagnosisMapping(
            JAX_DATA_SOURCE, DIAGNOSIS, ORIGIN_TISSUE, "Non_existing_tumor_type");

    assertThat(mappingEntity, is(nullValue()));
  }

  @Test
  public void givenTreatmentMappingExistsWhenGetTreatmentMappingThenReturnData() {
    MappingEntity mappingEntity =
        testInstance.getTreatmentMapping(IRCC_CRC_DATA_SOURCE, TREATMENT_NAME);

    assertThat(mappingEntity, is(notNullValue()));
    assertThat(mappingEntity.getEntityType(), is(MappingEntityType.TREATMENT.getLabel()));
    Map<String, String> mappingValues = mappingEntity.getMappingValues();
    assertThat(mappingValues, is(notNullValue()));
    assertThat(mappingValues.get(TreatmentMappingLabels.DATA_SOURCE.getLabel()), is(IRCC_CRC_DATA_SOURCE));
    assertThat(
        mappingValues.get(TreatmentMappingLabels.TREATMENT_TYPE.getLabel()).toLowerCase(),
        is(TREATMENT_NAME.toLowerCase()));
  }

  @Test
  public void givenNonExistingDataSourceWhenGetTreatmentMappingThenReturnData() {
    MappingEntity mappingEntity =
        testInstance.getTreatmentMapping("Non_existing_data_source", TREATMENT_NAME);

    assertThat(mappingEntity, is(nullValue()));
  }

  @Test
  public void givenNonExistingTreatmentNameWhenGetTreatmentMappingThenReturnData() {
    MappingEntity mappingEntity =
        testInstance.getTreatmentMapping(IRCC_CRC_DATA_SOURCE, "Non_existing_treatment_type");

    assertThat(mappingEntity, is(nullValue()));
  }

  @Test
  public void whenGetMappingRulesPathsThenReturnValues() {
    Map<String, String> mappingRulesPaths = testInstance.getMappingRulesPaths();

    assertThat(mappingRulesPaths, is(notNullValue()));
    String diagnosisMappingPath = mappingRulesPaths.get(MappingEntityType.DIAGNOSIS.getLabel());
    assertThat(
        diagnosisMappingPath, is("src/test/resources/test_data/mapping/diagnosis_mappings.json"));
    String treatmentMappingPath = mappingRulesPaths.get(MappingEntityType.TREATMENT.getLabel());
    assertThat(
        treatmentMappingPath, is("src/test/resources/test_data/mapping/treatment_mappings.json"));
  }

  @Test
  public void givenDiagnosisJsonFileNotExistsWhenGetMappingRulesPathsThenReturnValues() {
    testInstance = spy(testInstance);
    doReturn("diagnosis_mappings_no_exists.json").when(testInstance).getDiagnosisJsonFileName();
    String mappingDirectory = testInstance.getMappingDirectory();
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> testInstance.getMappingRulesPaths(),
        "Expected testInstance.getMappingRulesPaths() to throw IllegalArgumentException, but it didn't"
    );

    assertThat(
        thrown.getMessage(),
        is(String.format(
            MAPPING_RULE_NOT_FOUND,
            MappingEntityType.DIAGNOSIS.getLabel(),
            mappingDirectory + "/diagnosis_mappings_no_exists.json")));

    assertThrows(IllegalArgumentException.class, () -> {
      testInstance.getMappingRulesPaths();
    });
  }

  @Test
  public void givenTreatmentJsonFileNotExistsWhenGetMappingRulesPathsThenReturnValues() {
    testInstance = spy(testInstance);
    doReturn("treatment_mappings_no_exists.json").when(testInstance).getDiagnosisJsonFileName();
    String mappingDirectory = testInstance.getMappingDirectory();
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> testInstance.getMappingRulesPaths(),
        "Expected testInstance.getMappingRulesPaths() to throw IllegalArgumentException, but it didn't"
    );

    assertThat(
        thrown.getMessage(),
        is(String.format(
            MAPPING_RULE_NOT_FOUND,
            MappingEntityType.DIAGNOSIS.getLabel(),
            mappingDirectory + "/treatment_mappings_no_exists.json")));

    assertThrows(IllegalArgumentException.class, () -> {
      testInstance.getMappingRulesPaths();
    });
  }

  private List<MappingEntity> getExpectedMappingEntities() {
    List<MappingEntity> expectedMappingEntities = new ArrayList<>();

    MappingEntity mappingEntity1 = new MappingEntity();
    mappingEntity1.setEntityId(1L);
    mappingEntity1.setMappingKey("x");
    mappingEntity1.setEntityType(MappingEntityType.DIAGNOSIS.getLabel());
    mappingEntity1.setMappingLabels(testInstance.getDiagnosisMappingLabels());
    mappingEntity1.setMappedTermLabel("Acute Myeloid Leukemia");
    mappingEntity1.setMappedTermUrl("http://purl.obolibrary.org/obo/NCIT_C3171");
    mappingEntity1.setMapType("direct");
    mappingEntity1.setJustification("0");
    Map<String, String> values1 = new HashMap<>();
    values1.put(DiagnosisMappingLabels.DATA_SOURCE.getLabel(), "jax");
    values1.put(DiagnosisMappingLabels.SAMPLE_DIAGNOSIS.getLabel(), "acute myeloid leukemia");
    values1.put(DiagnosisMappingLabels.ORIGIN_TISSUE.getLabel(), "blood");
    values1.put(DiagnosisMappingLabels.TUMOR_TYPE.getLabel(), "primary");
    mappingEntity1.setMappingValues(values1);
    mappingEntity1.setSuggestedMappings(Collections.emptyList());
    mappingEntity1.setStatus("validated");

    MappingEntity mappingEntity2 = new MappingEntity();
    mappingEntity2.setEntityId(2L);
    mappingEntity2.setMappingKey("x");
    mappingEntity2.setEntityType(MappingEntityType.DIAGNOSIS.getLabel());
    mappingEntity2.setMappingLabels(testInstance.getDiagnosisMappingLabels());
    mappingEntity2.setMappedTermLabel("Acute Myeloid Leukemia");
    mappingEntity2.setMappedTermUrl("http://purl.obolibrary.org/obo/NCIT_C3171");
    Map<String, String> values2 = new HashMap<>();
    values2.put(DiagnosisMappingLabels.DATA_SOURCE.getLabel(), "jax");
    values2.put(DiagnosisMappingLabels.SAMPLE_DIAGNOSIS.getLabel(), "acute myeloid leukemia");
    values2.put(DiagnosisMappingLabels.ORIGIN_TISSUE.getLabel(), "bone marrow");
    values2.put(DiagnosisMappingLabels.TUMOR_TYPE.getLabel(), "primary");
    mappingEntity2.setMappingValues(values2);
    mappingEntity2.setSuggestedMappings(Collections.emptyList());
    mappingEntity2.setMapType("direct");
    mappingEntity2.setJustification("0");
    mappingEntity2.setStatus("validated");

    MappingEntity mappingEntity3 = new MappingEntity();
    mappingEntity3.setEntityId(3L);
    mappingEntity3.setMappingKey("x");
    mappingEntity3.setEntityType(MappingEntityType.TREATMENT.getLabel());
    mappingEntity3.setMappingLabels(testInstance.getTreatmentMappingLabels());
    mappingEntity3.setMappedTermLabel("Saline");
    mappingEntity3.setMappedTermUrl("http://purl.obolibrary.org/obo/NCIT_C821");
    Map<String, String> values3 = new HashMap<>();
    values3.put(TreatmentMappingLabels.DATA_SOURCE.getLabel(), "ircc-crc");
    values3.put(TreatmentMappingLabels.TREATMENT_TYPE.getLabel(), "0.9% solution of sodium chloride");
    mappingEntity3.setMappingValues(values3);
    mappingEntity3.setSuggestedMappings(Collections.emptyList());
    mappingEntity3.setMapType("inferred");
    mappingEntity3.setJustification("0");
    mappingEntity3.setStatus("validated");

    MappingEntity mappingEntity4 = new MappingEntity();
    mappingEntity4.setEntityId(4L);
    mappingEntity4.setMappingKey("x");
    mappingEntity4.setEntityType(MappingEntityType.TREATMENT.getLabel());
    mappingEntity4.setMappingLabels(testInstance.getTreatmentMappingLabels());
    mappingEntity4.setMappedTermLabel("Cetuximab");
    mappingEntity4.setMappedTermUrl("http://purl.obolibrary.org/obo/NCIT_C1723");
    Map<String, String> values4 = new HashMap<>();
    values4.put(TreatmentMappingLabels.DATA_SOURCE.getLabel(), "ircc-crc");
    values4.put(TreatmentMappingLabels.TREATMENT_TYPE.getLabel(), "erbitux, cetuximab");
    mappingEntity4.setMappingValues(values4);
    mappingEntity4.setSuggestedMappings(Collections.emptyList());
    mappingEntity4.setMapType("inferred");
    mappingEntity4.setJustification("0");
    mappingEntity4.setStatus("Created");

    expectedMappingEntities.add(mappingEntity1);
    expectedMappingEntities.add(mappingEntity2);
    expectedMappingEntities.add(mappingEntity3);
    expectedMappingEntities.add(mappingEntity4);

    return expectedMappingEntities;
  }

  @Test
  public void givenMappingRulesExistWhenRebuildDatabaseFromRulesFilesThenSuccess() {
    testInstance.rebuildDatabaseFromRulesFiles();
    List<MappingEntity> mappingEntities = testInstance.getMappingContainer().getEntityList();

    verify(mappingEntityRepository, times(1)).saveAll(mappingEntities);
    assertThat(mappingEntities.size(), is(4));
    List<MappingEntity> expectedMappingEntities = getExpectedMappingEntities();
    System.out.println(expectedMappingEntities);
    assertThat(
        MappingEntityUtil.findById(mappingEntities, 1L),
        is( MappingEntityUtil.findById(expectedMappingEntities, 1L)));
    assertThat(
        MappingEntityUtil.findById(mappingEntities, 2L),
        is( MappingEntityUtil.findById(expectedMappingEntities, 2L)));
    assertThat(
        MappingEntityUtil.findById(mappingEntities, 3L),
        is( MappingEntityUtil.findById(expectedMappingEntities, 3L)));
    assertThat(
        MappingEntityUtil.findById(mappingEntities, 4L),
        is( MappingEntityUtil.findById(expectedMappingEntities, 4L)));
  }
}
