package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.common.GlobalTags;
import com.linkedin.common.InstitutionalMemory;
import com.linkedin.common.Ownership;
import com.linkedin.common.Status;
import com.linkedin.datahub.graphql.generated.DataPlatform;
import com.linkedin.datahub.graphql.generated.Datasource;
import com.linkedin.datahub.graphql.generated.DatasourceEditableProperties;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.generated.FabricType;
import com.linkedin.datahub.graphql.types.common.mappers.InstitutionalMemoryMapper;
import com.linkedin.datahub.graphql.types.common.mappers.OwnershipMapper;
import com.linkedin.datahub.graphql.types.common.mappers.StatusMapper;
import com.linkedin.datahub.graphql.types.common.mappers.StringMapMapper;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;
import com.linkedin.datahub.graphql.types.tag.mappers.GlobalTagsMapper;
import com.linkedin.datasource.DatasourceDeprecation;
import com.linkedin.datasource.DatasourceProperties;
import com.linkedin.datasource.EditableDatasourceProperties;
import com.linkedin.metadata.dao.utils.ModelUtils;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;
import com.linkedin.schema.EditableSchemaMetadata;
import com.linkedin.schema.SchemaMetadata;

import javax.annotation.Nonnull;
import java.util.ArrayList;


/**
 * Maps Pegasus {@link RecordTemplate} objects to objects conforming to the GQL schema.
 *
 * To be replaced by auto-generated mappers implementations
 */
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
        result.setOrigin(Enum.valueOf(FabricType.class, datasource.getUrn().getOriginEntity().toString()));

        DataPlatform partialPlatform = new DataPlatform();
        partialPlatform.setUrn(datasource.getUrn().getPlatformEntity().toString());
        result.setPlatform(partialPlatform);

        ModelUtils.getAspectsFromSnapshot(datasource).forEach(aspect -> {
            result.setTags(new ArrayList<>());
            if (aspect instanceof DatasourceProperties) {
                final DatasourceProperties datasourceProperties = (DatasourceProperties) aspect;
                result.setProperties(StringMapMapper.map(datasourceProperties.getCustomProperties()));
                if (datasourceProperties.getUri() != null) {
                  result.setUri(datasourceProperties.getUri().toString());
                }
                if (datasourceProperties.getDescription() != null) {
                  result.setDescription(datasourceProperties.getDescription());
                }
                if (datasourceProperties.getExternalUrl() != null) {
                  result.setExternalUrl(datasourceProperties.getExternalUrl().toString());
                }
            } else if (aspect instanceof DatasourceDeprecation) {
                result.setDeprecation(DatasourceDeprecationMapper.map((DatasourceDeprecation) aspect));
            } else if (aspect instanceof InstitutionalMemory) {
                result.setInstitutionalMemory(InstitutionalMemoryMapper.map((InstitutionalMemory) aspect));
            } else if (aspect instanceof Ownership) {
                result.setOwnership(OwnershipMapper.map((Ownership) aspect));
            } else if (aspect instanceof SchemaMetadata) {
                result.setSchema(
                    SchemaMapper.map((SchemaMetadata) aspect)
                );
            } else if (aspect instanceof Status) {
              result.setStatus(StatusMapper.map((Status) aspect));
            } else if (aspect instanceof GlobalTags) {
              result.setGlobalTags(GlobalTagsMapper.map((GlobalTags) aspect));
            } else if (aspect instanceof EditableSchemaMetadata) {
              result.setEditableSchemaMetadata(EditableSchemaMetadataMapper.map((EditableSchemaMetadata) aspect));
            } else if (aspect instanceof EditableDatasourceProperties) {
                final EditableDatasourceProperties editableDatasourceProperties = (EditableDatasourceProperties) aspect;
                final DatasourceEditableProperties editableProperties = new DatasourceEditableProperties();
                editableProperties.setDescription(editableDatasourceProperties.getDescription());
                result.setEditableProperties(editableProperties);
            }
        });

        return result;
    }
}
