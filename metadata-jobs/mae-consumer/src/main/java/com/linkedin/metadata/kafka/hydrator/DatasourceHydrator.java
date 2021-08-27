package com.linkedin.metadata.kafka.hydrator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linkedin.metadata.aspect.DatasourceAspect;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DatasourceHydrator extends BaseHydrator<DatasourceSnapshot> {

  private static final String CATEGORY = "category";
  private static final String PLATFORM = "platform";
  private static final String NAME = "name";

  @Override
  protected void hydrateFromSnapshot(ObjectNode document, DatasourceSnapshot snapshot) {
    for (DatasourceAspect aspect : snapshot.getAspects()) {
      if (aspect.isDatasourceKey()) {
        document.put(CATEGORY, aspect.getDatasourceKey().getCategory().toString());
        document.put(NAME, aspect.getDatasourceKey().getName());
      }
      if (aspect.isDatasourceConnections()) {
        document.put(PLATFORM, aspect.getDatasourceConnections().getPlatform().toString());


      }
    }
  }
}
