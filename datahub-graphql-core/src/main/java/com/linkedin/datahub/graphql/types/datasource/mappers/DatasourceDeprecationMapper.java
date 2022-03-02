package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.datahub.graphql.generated.DatasourceDeprecation;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;

import javax.annotation.Nonnull;

public class DatasourceDeprecationMapper implements ModelMapper<com.linkedin.datasource.DatasourceDeprecation, DatasourceDeprecation> {

    public static final DatasourceDeprecationMapper INSTANCE = new DatasourceDeprecationMapper();

    public static DatasourceDeprecation map(@Nonnull final com.linkedin.datasource.DatasourceDeprecation deprecation) {
        return INSTANCE.apply(deprecation);
    }

    @Override
    public DatasourceDeprecation apply(@Nonnull final com.linkedin.datasource.DatasourceDeprecation input) {
        final DatasourceDeprecation result = new DatasourceDeprecation();
        result.setActor(input.getActor().toString());
        result.setDeprecated(input.isDeprecated());
        result.setDecommissionTime(input.getDecommissionTime());
        result.setNote(input.getNote());
        return result;
    }
}
