package com.linkedin.metadata.urn.datasource;

import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.metadata.dao.scsi.UrnPathExtractor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public final class DatasourceUrnPathExtractor implements UrnPathExtractor<DatasourceUrn> {
  @Nonnull
  @Override
  public Map<String, Object> extractPaths(@Nonnull DatasourceUrn urn) {
    return Collections.unmodifiableMap(new HashMap<String, String>() {
      {
        put("/platform", urn.getPlatformEntity().toString());
        put("/datasourceName", urn.getDatasourceNameEntity());
        put("/origin", urn.getOriginEntity().toString());
        put("/platform/platformName", urn.getPlatformEntity().getPlatformNameEntity());
      }
    });
  }
}