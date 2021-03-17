package org.pdxfinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.pdxfinder.constants.DiagnosisMappingLabels;
import org.pdxfinder.constants.MappingEntityType;
import org.pdxfinder.constants.TreatmentMappingLabels;
import org.pdxfinder.repositories.MappingEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class MappingServiceTest {

  @Mock
  private MappingEntityRepository mappingEntityRepository;
  @Mock
  private UtilityService utilityService;
  @Mock
  private PaginationService paginationService;

  private static final String JAX_DATA_SOURCE = "jax";
  private static final String DIAGNOSIS = "acute myeloid leukemia";
  private static final String ORIGIN_TISSUE = "blood";
  private static final String TUMOR_TYPE = "primary";

  private static final String IRCC_CRC_DATA_SOURCE = "ircc-crc";
  private static final String TREATMENT_NAME = "0.9% Solution of Sodium Chloride";

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
}
