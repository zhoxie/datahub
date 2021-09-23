package com.linkedin.datahub.graphql.types.lineage;

import com.google.common.collect.ImmutableList;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.DatasourceDatasetsRelationships;
import com.linkedin.datahub.graphql.types.LoadableType;
import com.linkedin.datahub.graphql.types.relationships.mappers.DatasourceDatasetsRelationshipsMapper;
import com.linkedin.lineage.client.RelationshipClient;
import com.linkedin.metadata.query.RelationshipDirection;
import com.linkedin.r2.RemoteInvocationException;
import graphql.execution.DataFetcherResult;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class SourceRelationshipsType implements LoadableType<DatasourceDatasetsRelationships> {

    private final RelationshipClient _relationshipClientClient;
    private final RelationshipDirection _direction = RelationshipDirection.INCOMING;

    public SourceRelationshipsType(final RelationshipClient relationshipClientClient) {
        _relationshipClientClient = relationshipClientClient;
    }

    @Override
    public Class<DatasourceDatasetsRelationships> objectClass() {
        return DatasourceDatasetsRelationships.class;
    }

    @Override
    public List<DataFetcherResult<DatasourceDatasetsRelationships>> batchLoad(final List<String> keys, final QueryContext context) {
        try {
            return keys.stream().map(urn -> {
                try {
                    com.linkedin.common.EntityRelationships relationships =
                            _relationshipClientClient.getRelationships(
                                urn,
                                _direction,
                                ImmutableList.of("Sources"),
                                null,
                                null,
                                context.getActor());
                    return DataFetcherResult.<DatasourceDatasetsRelationships>newResult()
                            .data(DatasourceDatasetsRelationshipsMapper.map(relationships)).build();
                } catch (RemoteInvocationException | URISyntaxException e) {
                    throw new RuntimeException(String.format("Failed to batch load DataJobs for DataFlow %s", urn), e);
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to batch load DataJobs for DataFlow", e);
        }
    }
}
