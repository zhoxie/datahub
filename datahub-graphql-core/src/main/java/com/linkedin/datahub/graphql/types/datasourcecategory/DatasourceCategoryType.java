package com.linkedin.datahub.graphql.types.datasourcecategory;

import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.DatasourceCategory;
import com.linkedin.datahub.graphql.types.EntityType;
import com.linkedin.datahub.graphql.types.datasourcecategory.mappers.DatasourceCategorySnapshotMapper;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.extractor.AspectExtractor;
import graphql.execution.DataFetcherResult;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatasourceCategoryType implements EntityType<DatasourceCategory> {

    private final EntityClient _entityClient;

    public DatasourceCategoryType(final EntityClient entityClient) {
        _entityClient = entityClient;
    }

    @Override
    public Class<DatasourceCategory> objectClass() {
        return DatasourceCategory.class;
    }

    @Override
    public List<DataFetcherResult<DatasourceCategory>> batchLoad(final List<String> urns, final QueryContext context) {

        final List<Urn> datasourceCategoryUrns = urns.stream()
            .map(urnStr -> {
                try {
                    return Urn.createFromString(urnStr);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(String.format("Failed to retrieve entity with urn %s", urnStr));
                }
            })
            .collect(Collectors.toList());

        try {
            final Map<Urn, com.linkedin.entity.Entity> datasourceCategoryMap = _entityClient.batchGet(datasourceCategoryUrns
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()),
                    context.getActor());

            final List<com.linkedin.entity.Entity> gmsResults = new ArrayList<>();
            for (Urn urn : datasourceCategoryUrns) {
                gmsResults.add(datasourceCategoryMap.getOrDefault(urn, null));
            }

            return gmsResults.stream()
                .map(gmsCategory -> gmsCategory == null ? null
                    : DataFetcherResult.<DatasourceCategory>newResult()
                        .data(DatasourceCategorySnapshotMapper.map(gmsCategory.getValue().getDatasourceCategorySnapshot()))
                        .localContext(AspectExtractor.extractAspects(
                            gmsCategory.getValue().getDatasourceCategorySnapshot()))
                        .build())
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to batch load Data Categories", e);
        }
    }

    @Override
    public com.linkedin.datahub.graphql.generated.EntityType type() {
        return com.linkedin.datahub.graphql.generated.EntityType.DATASOURCE_CATEGORY;
    }


}
