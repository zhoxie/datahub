package com.linkedin.datahub.graphql.types.datasourcecategory;

import com.linkedin.common.urn.DatasourceCategoryUrn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.AllDatasourceCategory;
import com.linkedin.datahub.graphql.generated.DatasourceCategory;
import com.linkedin.datahub.graphql.generated.DatasourceCategoryInfo;
import com.linkedin.datahub.graphql.types.EntityType;
import com.linkedin.datasourcecategory.client.DatasourceCategories;
import com.linkedin.r2.RemoteInvocationException;
import graphql.execution.DataFetcherResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AllDatasourceCategories implements EntityType<AllDatasourceCategory> {

    private final DatasourceCategories _datasourceCategories;

    public AllDatasourceCategories(final DatasourceCategories datasourceCategories) {
        _datasourceCategories = datasourceCategories;
    }

    @Override
    public Class<AllDatasourceCategory> objectClass() {
        return AllDatasourceCategory.class;
    }

    @Override
    public List<DataFetcherResult<AllDatasourceCategory>> batchLoad(final List<String> urns, final QueryContext context) {
        try {

            List<DatasourceCategory> categories = _datasourceCategories.getAllCategories().stream()
                    .map(dc -> {
                        DatasourceCategory result = new DatasourceCategory();
                        DatasourceCategoryUrn urn = new DatasourceCategoryUrn(dc.getName());
                        result.setUrn(urn.toString());
                        result.setName(dc.getName());
                        result.setDisplayName(dc.getDatasourceCategoryInfo().getDisplayName());
                        DatasourceCategoryInfo info = new DatasourceCategoryInfo();
                        info.setDisplayName(dc.getDatasourceCategoryInfo().getDisplayName());
                        result.setType(com.linkedin.datahub.graphql.generated.EntityType.DATASOURCE_CATEGORY);
                        return result;
                    })
                    .collect(Collectors.toList());
            AllDatasourceCategory allCategories = new AllDatasourceCategory();
            allCategories.setUrn(urns.get(0));
            allCategories.setType(com.linkedin.datahub.graphql.generated.EntityType.ALL_DATASOURCE_CATEGORIES);
            allCategories.setCategories(categories);

            return Collections.singletonList(DataFetcherResult.<AllDatasourceCategory>newResult().data(allCategories).build());
        } catch (RemoteInvocationException e) {
            throw new RuntimeException("Failed to batch load Data Categories", e);
        }
    }

    @Override
    public com.linkedin.datahub.graphql.generated.EntityType type() {
        return com.linkedin.datahub.graphql.generated.EntityType.ALL_DATASOURCE_CATEGORIES;
    }


}
