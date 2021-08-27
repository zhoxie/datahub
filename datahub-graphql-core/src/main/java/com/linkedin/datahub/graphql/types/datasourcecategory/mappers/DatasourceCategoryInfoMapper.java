package com.linkedin.datahub.graphql.types.datasourcecategory.mappers;

import com.linkedin.datahub.graphql.generated.DatasourceCategoryInfo;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;

import javax.annotation.Nonnull;

public class DatasourceCategoryInfoMapper implements ModelMapper<com.linkedin.datasourcecategory.DatasourceCategoryInfo, DatasourceCategoryInfo> {

    public static final DatasourceCategoryInfoMapper INSTANCE = new DatasourceCategoryInfoMapper();

    public static DatasourceCategoryInfo map(@Nonnull final com.linkedin.datasourcecategory.DatasourceCategoryInfo category) {
        return INSTANCE.apply(category);
    }

    @Override
    public DatasourceCategoryInfo apply(@Nonnull final com.linkedin.datasourcecategory.DatasourceCategoryInfo input) {
        final DatasourceCategoryInfo result = new DatasourceCategoryInfo();
        result.setDisplayName(input.getDisplayName());
        return result;
    }
}
