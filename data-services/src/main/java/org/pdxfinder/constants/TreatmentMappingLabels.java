package org.pdxfinder.constants;

import java.util.HashMap;
import java.util.Map;

public enum TreatmentMappingLabels {
  DATA_SOURCE("DataSource"),
  TREATMENT_TYPE("TreatmentName");

  private static final Map<String, TreatmentMappingLabels> BY_LABEL = new HashMap<>();
  static
  {
    for (TreatmentMappingLabels e: values())
    {
      BY_LABEL.put(e.label, e);
    }
  }

  private final String label;

  TreatmentMappingLabels(String label)
  {
    this.label = label;
  }

  public static TreatmentMappingLabels valueOfLabel(String label)
  {
    return BY_LABEL.get(label);
  }

  public String getLabel()
  {
    return label;
  }
}
