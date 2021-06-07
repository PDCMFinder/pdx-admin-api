package org.pdxfinder.constants;

import java.util.HashMap;
import java.util.Map;

public enum DiagnosisMappingLabels {
  DATA_SOURCE("DataSource"),
  ORIGIN_TISSUE("OriginTissue"),
  TUMOR_TYPE("TumorType"),
  SAMPLE_DIAGNOSIS("SampleDiagnosis");

  private static final Map<String, DiagnosisMappingLabels> BY_LABEL = new HashMap<>();
  static
  {
    for (DiagnosisMappingLabels e: values())
    {
      BY_LABEL.put(e.label, e);
    }
  }

  private final String label;

  DiagnosisMappingLabels(String label)
  {
    this.label = label;
  }

  public static DiagnosisMappingLabels valueOfLabel(String label)
  {
    return BY_LABEL.get(label);
  }

  public String getLabel()
  {
    return label;
  }
}
