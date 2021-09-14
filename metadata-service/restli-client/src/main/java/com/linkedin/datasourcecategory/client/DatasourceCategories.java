package com.linkedin.datasourcecategory.client;

import com.linkedin.data.template.StringArray;
import com.linkedin.datasourcecategory.DatasourceCategoryInfo;
import com.linkedin.datasourceCategories.DatasourceCategory;
import com.linkedin.datasourcecategory.DatasourceCategoriesRequestBuilders;
import com.linkedin.metadata.restli.BaseClient;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.GetAllRequest;
import com.linkedin.restli.client.GetRequest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class DatasourceCategories extends BaseClient {

  private static final DatasourceCategoriesRequestBuilders CATEGORIES_REQUEST_BUILDERS = new DatasourceCategoriesRequestBuilders();

  public DatasourceCategories(@Nonnull Client restliClient) {
    super(restliClient);
  }

  /**
   * Get datasource category details by name
   * @param categoryName String
   * @return DatasourceCategoryInfo
   * @throws RemoteInvocationException
   */
  @Nonnull
  public DatasourceCategoryInfo getCategoryByName(@Nonnull String categoryName) throws RemoteInvocationException {
    final GetRequest<DatasourceCategory> req = CATEGORIES_REQUEST_BUILDERS.get()
        .id(categoryName)
        .aspectsParam(new StringArray(DatasourceCategoryInfo.class.getCanonicalName()))
        .build();
    return _client.sendRequest(req).getResponse().getEntity().getDatasourceCategoryInfo();
  }

  /**
   * Get all datasource categories
   * @return List<DatasourceCategoryInfo>
   * @throws RemoteInvocationException
   */
  @Nonnull
  public List<DatasourceCategory> getAllCategories() throws RemoteInvocationException {
    final GetAllRequest<DatasourceCategory> req = CATEGORIES_REQUEST_BUILDERS.getAll().build();
    return new ArrayList<>(_client.sendRequest(req)
            .getResponse()
            .getEntity()
            .getElements());
  }
}
