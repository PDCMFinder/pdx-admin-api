package org.pdxfinder.util;

import java.util.List;
import org.pdxfinder.MappingEntity;

public class MappingEntityUtil {

  public static MappingEntity findById(List<MappingEntity> mappingEntities, Long id) {
    return mappingEntities.stream().filter(x -> id.equals(x.getEntityId())).findFirst().orElse(null);
  }
}
