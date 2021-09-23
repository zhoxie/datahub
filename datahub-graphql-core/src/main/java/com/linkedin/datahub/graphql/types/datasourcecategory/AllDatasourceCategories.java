package com.linkedin.datahub.graphql.types.datasourcecategory;

import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.AllDatasourceCategory;
import com.linkedin.datahub.graphql.generated.DatasourceCategory;
import com.linkedin.datahub.graphql.types.EntityType;
import com.linkedin.datahub.graphql.types.datasourcecategory.mappers.DatasourceCategorySnapshotMapper;
import com.linkedin.entity.Entity;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.query.ListUrnsResult;
import graphql.execution.DataFetcherResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AllDatasourceCategories implements EntityType<AllDatasourceCategory> {

    private final EntityClient _entityClient;
    private static final String ENTITY_NAME = "datasourceCategory";

    public AllDatasourceCategories(final EntityClient entityClient) {
        _entityClient = entityClient;
    }

    @Override
    public Class<AllDatasourceCategory> objectClass() {
        return AllDatasourceCategory.class;
    }

    @Override
    public List<DataFetcherResult<AllDatasourceCategory>> batchLoad(final List<String> urns, final QueryContext context) {
        try {
            List<Urn> urnList = getAll(context);
            final Map<Urn, Entity> datasourceCategoryMap = _entityClient.batchGet(urnList
                            .stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet()),
                    context.getActor());

            List<DatasourceCategory> list = datasourceCategoryMap.values().stream().map(entity ->
                    DatasourceCategorySnapshotMapper.map(
                            entity.getValue().getDatasourceCategorySnapshot())
            ).collect(Collectors.toList());

            AllDatasourceCategory allCategories = new AllDatasourceCategory();
            allCategories.setUrn(urns.get(0));
            allCategories.setType(com.linkedin.datahub.graphql.generated.EntityType.ALL_DATASOURCE_CATEGORIES);
            allCategories.setCategories(list);

            return Collections.singletonList(DataFetcherResult.<AllDatasourceCategory>newResult().data(allCategories).build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to batch load Data Categories", e);
        }
    }

    private List<Urn> getAll(final QueryContext context) {
        int start = 0;
        int count = 100;
        int actual;
        List<Urn> urns = new ArrayList<>();
        try {
            do {
                ListUrnsResult res = _entityClient.listUrns(ENTITY_NAME, start, count, context.getActor());
                actual = res.getEntities().size();
                urns.addAll(res.getEntities());
                start += count;
            } while (actual == count);

            return urns;
        } catch (Exception e) {
            throw new RuntimeException("Failed to batch load Data Categories", e);
        }

    }

    @Override
    public com.linkedin.datahub.graphql.generated.EntityType type() {
        return com.linkedin.datahub.graphql.generated.EntityType.ALL_DATASOURCE_CATEGORIES;
    }


}
