package com.linkedin.metadata.kafka.hydrator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linkedin.data.DataMap;
import com.linkedin.datahub.graphql.types.common.mappers.util.MappingHelper;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.entity.EntityResponse;
import com.linkedin.entity.EnvelopedAspectMap;
import com.linkedin.metadata.key.DatasourceKey;
import lombok.extern.slf4j.Slf4j;

import static com.linkedin.metadata.Constants.DATASOURCE_INFO_ASPECT_NAME;
import static com.linkedin.metadata.Constants.DATASOURCE_KEY_ASPECT_NAME;


@Slf4j
public class DatasourceHydrator extends BaseHydrator {

  private static final String PLATFORM = "platform";
  private static final String NAME = "name";
  private static final String REGION = "region";
  private static final String GROUP = "group";

  protected void hydrateFromEntityResponse(ObjectNode document, EntityResponse entityResponse) {
    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    MappingHelper<ObjectNode> mappingHelper = new MappingHelper<>(aspectMap, document);
    mappingHelper.mapToResult(DATASOURCE_KEY_ASPECT_NAME, this::mapKey);
    mappingHelper.mapToResult(DATASOURCE_INFO_ASPECT_NAME, this::infoKey);
  }

  private void mapKey(ObjectNode jsonNodes, DataMap dataMap) {
    DatasourceKey datasourceKey = new DatasourceKey(dataMap);
    jsonNodes.put(PLATFORM, datasourceKey.getPlatform().toString());
    jsonNodes.put(NAME, datasourceKey.getName());
    jsonNodes.put(REGION, datasourceKey.getRegion());
  }

  private void infoKey(ObjectNode jsonNodes, DataMap dataMap) {
    DatasourceInfo datasourceInfo = new DatasourceInfo(dataMap);
    if (datasourceInfo.hasGroup()) {
      jsonNodes.put(GROUP, datasourceInfo.getGroup().toString());

    }
  }
}
