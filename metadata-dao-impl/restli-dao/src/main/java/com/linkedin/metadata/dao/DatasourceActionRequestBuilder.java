package com.linkedin.metadata.dao;

import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;


/**
 * An action request builder for corp user info entities.
 */
public class DatasourceActionRequestBuilder extends BaseActionRequestBuilder<DatasourceSnapshot, DatasourceUrn> {

  private static final String BASE_URI_TEMPLATE = "datasources";

  public DatasourceActionRequestBuilder() {
    super(DatasourceSnapshot.class, DatasourceUrn.class, BASE_URI_TEMPLATE);
  }
}