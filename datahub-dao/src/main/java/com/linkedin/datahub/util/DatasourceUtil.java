package com.linkedin.datahub.util;

import com.linkedin.common.urn.DatasourceUrn;
import java.net.URISyntaxException;



public class DatasourceUtil {
  private DatasourceUtil() {
  }

  static DatasourceUrn getDatasourceUrn(String urnStr) {
    try {
      return DatasourceUrn.createFromString(urnStr);
    } catch (URISyntaxException e) {
      throw new RuntimeException(String.format("Failed to retrieve dataset with urn %s, invalid urn", urnStr));
    }
  }
}
