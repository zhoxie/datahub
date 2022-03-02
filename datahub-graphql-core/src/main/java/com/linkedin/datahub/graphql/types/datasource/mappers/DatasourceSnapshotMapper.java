package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.common.GlobalTags;
import com.linkedin.common.InstitutionalMemory;
import com.linkedin.common.Ownership;
import com.linkedin.common.Status;
import com.linkedin.datahub.graphql.generated.Datasource;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.generated.DataPlatform;
import com.linkedin.datahub.graphql.generated.FabricType;
import com.linkedin.datahub.graphql.generated.CorpGroup;
import com.linkedin.datahub.graphql.generated.DatasourceEditableProperties;
import com.linkedin.datahub.graphql.types.common.mappers.InstitutionalMemoryMapper;
import com.linkedin.datahub.graphql.types.common.mappers.OwnershipMapper;
import com.linkedin.datahub.graphql.types.common.mappers.StatusMapper;
import com.linkedin.datahub.graphql.types.common.mappers.StringMapMapper;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;
import com.linkedin.datahub.graphql.types.tag.mappers.GlobalTagsMapper;
import com.linkedin.datasource.DatasourceDeprecation;
import com.linkedin.datasource.DatasourceProperties;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.datasource.EditableDatasourceProperties;
import com.linkedin.datasource.DatasourceConnectionPrimary;
import com.linkedin.datasource.DatasourceConnectionGSB;
import com.linkedin.datasource.DatasourceCustomDashboardInfo;
import com.datahub.util.ModelUtils;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * Maps GMS response objects to objects conforming to the GQL schema.
 *
 * To be replaced by auto-generated mappers implementations
 */
@Slf4j
public class DatasourceSnapshotMapper implements ModelMapper<DatasourceSnapshot, Datasource> {

    public static final DatasourceSnapshotMapper INSTANCE = new DatasourceSnapshotMapper();

    public static Datasource map(@Nonnull final DatasourceSnapshot datasource) {
        return INSTANCE.apply(datasource);
    }

    @Override
    public Datasource apply(@Nonnull final DatasourceSnapshot datasource) {
        Datasource result = new Datasource();
        result.setUrn(datasource.getUrn().toString());
        result.setType(EntityType.DATASOURCE);
        result.setName(datasource.getUrn().getDatasourceNameEntity());
        result.setRegion(datasource.getUrn().getRegionEntity());
        result.setOrigin(FabricType.PROD);
        DataPlatform partialPlatform = new DataPlatform();
        partialPlatform.setUrn(datasource.getUrn().getPlatformEntity().toString());
        result.setPlatform(partialPlatform);
        result.setSyncCDAPI(false);

        ModelUtils.getAspectsFromSnapshot(datasource).forEach(aspect -> {
            if (aspect instanceof DatasourceProperties) {
                final DatasourceProperties datasourceProperties = (DatasourceProperties) aspect;
                com.linkedin.datahub.graphql.generated.DatasourceProperties dsProperties =
                        new com.linkedin.datahub.graphql.generated.DatasourceProperties();
                dsProperties.setDescription(datasourceProperties.getDescription());
                dsProperties.setCustomProperties(StringMapMapper.map(datasourceProperties.getCustomProperties()));
                dsProperties.setExternalUrl(datasourceProperties.getExternalUrl().toString());
                result.setProperties(dsProperties);
                if (datasourceProperties.getUri() != null) {
                  result.setUri(datasourceProperties.getUri().toString());
                }
                if (datasourceProperties.getDescription() != null) {
                  result.setDescription(datasourceProperties.getDescription());
                }
                if (datasourceProperties.getExternalUrl() != null) {
                  result.setExternalUrl(datasourceProperties.getExternalUrl().toString());
                }
            } else if (aspect instanceof DatasourceInfo) {
                DatasourceInfo datasourceInfo = (DatasourceInfo) aspect;
                CorpGroup group = new CorpGroup();
                group.setUrn(datasourceInfo.getGroup().toString());
                group.setName(datasourceInfo.getGroup().getGroupNameEntity());
                group.setType(EntityType.CORP_GROUP);
                result.setGroup(group);
                result.setRegion(datasourceInfo.getRegion());
            } else if (aspect instanceof DatasourceDeprecation) {
                result.setDeprecation(DatasourceDeprecationMapper.map((DatasourceDeprecation) aspect));
            } else if (aspect instanceof InstitutionalMemory) {
                result.setInstitutionalMemory(InstitutionalMemoryMapper.map((InstitutionalMemory) aspect));
            } else if (aspect instanceof Ownership) {
                result.setOwnership(OwnershipMapper.map((Ownership) aspect));
            } else if (aspect instanceof Status) {
              result.setStatus(StatusMapper.map((Status) aspect));
            } else if (aspect instanceof GlobalTags) {
              result.setGlobalTags(GlobalTagsMapper.map((GlobalTags) aspect));
            } else if (aspect instanceof EditableDatasourceProperties) {
                final EditableDatasourceProperties editableDatasourceProperties = (EditableDatasourceProperties) aspect;
                final DatasourceEditableProperties editableProperties = new DatasourceEditableProperties();
                editableProperties.setDescription(editableDatasourceProperties.getDescription());
                result.setEditableProperties(editableProperties);
            } else if (aspect instanceof DatasourceConnectionPrimary) {
                result.setPrimaryConn(DatasourceConnectionPrimaryMapper.map((DatasourceConnectionPrimary) aspect));
            } else if (aspect instanceof DatasourceConnectionGSB) {
                result.setGsbConn(DatasourceConnectionGSBMapper.map((DatasourceConnectionGSB) aspect));
            } else if (aspect instanceof DatasourceCustomDashboardInfo) {
                result.setSyncCDAPI(true);
            }
        });

        return result;
    }
}
