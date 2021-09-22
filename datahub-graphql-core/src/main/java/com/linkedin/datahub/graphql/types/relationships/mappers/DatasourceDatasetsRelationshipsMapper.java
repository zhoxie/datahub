package com.linkedin.datahub.graphql.types.relationships.mappers;

import com.linkedin.datahub.graphql.generated.DatasourceDatasetsRelationships;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class DatasourceDatasetsRelationshipsMapper implements
        ModelMapper<com.linkedin.common.EntityRelationships, DatasourceDatasetsRelationships> {

    public static final DatasourceDatasetsRelationshipsMapper INSTANCE = new DatasourceDatasetsRelationshipsMapper();

    public static DatasourceDatasetsRelationships map(
            @Nonnull final com.linkedin.common.EntityRelationships relationships) {
        return INSTANCE.apply(relationships);
    }

    @Override
    public DatasourceDatasetsRelationships apply(@Nonnull final com.linkedin.common.EntityRelationships input) {
        final DatasourceDatasetsRelationships result = new DatasourceDatasetsRelationships();
        result.setEntities(input.getRelationships().stream().map(
            EntityRelationshipLegacyMapper::map
        ).collect(Collectors.toList()));
        return result;
    }
}
