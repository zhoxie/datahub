package com.linkedin.datasource.client;

import com.linkedin.common.client.DatasourcesClient;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datasource.DatasourceDeprecation;
import com.linkedin.datasource.DeprecationRequestBuilders;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;

import javax.annotation.Nonnull;

public class Deprecations extends DatasourcesClient {
  private static final DeprecationRequestBuilders DEPRECATION_REQUEST_BUILDERS
      = new DeprecationRequestBuilders();

  public Deprecations(@Nonnull Client restliClient) {
    super(restliClient);
  }

  /**
   * Creates or Updates DatasourceDeprecation aspect
   *
   * @param datasourceUrn datasource urn
   * @param datasourceDeprecation datasource deprecation
   */
  public void updateDatasourceDeprecation(@Nonnull DatasourceUrn datasourceUrn,
      @Nonnull DatasourceDeprecation datasourceDeprecation)
      throws RemoteInvocationException {

    CreateIdRequest<Long, DatasourceDeprecation> request = DEPRECATION_REQUEST_BUILDERS.create()
        .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
        .input(datasourceDeprecation)
        .build();

    _client.sendRequest(request).getResponse();
  }

  /**
   * Get DatasourceDeprecation aspect
   *
   * @param datasourceUrn datasource urn
   */
  @Nonnull
  public DatasourceDeprecation getDatasourceDeprecation(@Nonnull DatasourceUrn datasourceUrn)
      throws RemoteInvocationException {

    Request<DatasourceDeprecation> request =
        DEPRECATION_REQUEST_BUILDERS.get()
            .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
            .id(BaseLocalDAO.LATEST_VERSION)
            .build();

    return _client.sendRequest(request).getResponse().getEntity();
  }
}
