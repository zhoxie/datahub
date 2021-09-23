package com.linkedin.datahub.graphql.types.datasourcecategory.mappers;

import com.linkedin.datahub.graphql.generated.DatasourceCategory;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;
import com.linkedin.metadata.aspect.DatasourceCategoryAspect;
import com.linkedin.metadata.snapshot.DatasourceCategorySnapshot;

import javax.annotation.Nonnull;


public class DatasourceCategorySnapshotMapper implements ModelMapper<DatasourceCategorySnapshot, DatasourceCategory> {

    public static final DatasourceCategorySnapshotMapper INSTANCE = new DatasourceCategorySnapshotMapper();

    public static DatasourceCategory map(@Nonnull final DatasourceCategorySnapshot category) {
        return INSTANCE.apply(category);
    }

    @Override
    public DatasourceCategory apply(@Nonnull final DatasourceCategorySnapshot input) {
        final DatasourceCategory result = new DatasourceCategory();
        result.setUrn(input.getUrn().toString());
        result.setName(input.getUrn().getCategoryNameEntity());

        for (DatasourceCategoryAspect aspect : input.getAspects()) {
            if (aspect.isDatasourceCategoryInfo()) {
                result.setInfo(DatasourceCategoryInfoMapper.map(aspect.getDatasourceCategoryInfo()));
            }
        }
        return result;
    }
}
