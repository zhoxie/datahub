package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.common.GlobalTags;
import com.linkedin.common.TagAssociationArray;
import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.generated.DatasourceUpdateInput;
import com.linkedin.datahub.graphql.types.common.mappers.InstitutionalMemoryUpdateMapper;
import com.linkedin.datahub.graphql.types.common.mappers.OwnershipUpdateMapper;
import com.linkedin.datahub.graphql.types.mappers.InputModelMapper;
import com.linkedin.datahub.graphql.types.tag.mappers.TagAssociationUpdateMapper;
import com.linkedin.datasource.Datasource;
import com.linkedin.datasource.DatasourceDeprecation;
import com.linkedin.datasource.EditableDatasourceProperties;
import com.linkedin.schema.EditableSchemaFieldInfo;
import com.linkedin.schema.EditableSchemaFieldInfoArray;
import com.linkedin.schema.EditableSchemaMetadata;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class DatasourceUpdateInputMapper implements InputModelMapper<DatasourceUpdateInput, Datasource, Urn> {

    public static final DatasourceUpdateInputMapper INSTANCE = new DatasourceUpdateInputMapper();

    public static Datasource map(@Nonnull final DatasourceUpdateInput datasourceUpdateInput,
                              @Nonnull final Urn actor) {
        return INSTANCE.apply(datasourceUpdateInput, actor);
    }

    @Override
    public Datasource apply(@Nonnull final DatasourceUpdateInput datasourceUpdateInput,
                         @Nonnull final Urn actor) {
        final Datasource result = new Datasource();

        if (datasourceUpdateInput.getOwnership() != null) {
            result.setOwnership(OwnershipUpdateMapper.map(datasourceUpdateInput.getOwnership(), actor));
        }

        if (datasourceUpdateInput.getDeprecation() != null) {
            final DatasourceDeprecation deprecation = new DatasourceDeprecation();
            deprecation.setDeprecated(datasourceUpdateInput.getDeprecation().getDeprecated());
            if (datasourceUpdateInput.getDeprecation().getDecommissionTime() != null) {
                deprecation.setDecommissionTime(datasourceUpdateInput.getDeprecation().getDecommissionTime());
            }
            deprecation.setNote(datasourceUpdateInput.getDeprecation().getNote());
            result.setDeprecation(deprecation);
        }

        if (datasourceUpdateInput.getInstitutionalMemory() != null) {
            result.setInstitutionalMemory(
                    InstitutionalMemoryUpdateMapper.map(datasourceUpdateInput.getInstitutionalMemory()));
        }

        if (datasourceUpdateInput.getGlobalTags() != null) {
            final GlobalTags globalTags = new GlobalTags();
            globalTags.setTags(
                    new TagAssociationArray(
                            datasourceUpdateInput.getGlobalTags().getTags().stream().map(
                                    element -> TagAssociationUpdateMapper.map(element)
                            ).collect(Collectors.toList())
                    )
            );
            result.setGlobalTags(globalTags);
        }

        if (datasourceUpdateInput.getEditableSchemaMetadata() != null) {
            final EditableSchemaMetadata editableSchemaMetadata = new EditableSchemaMetadata();
            editableSchemaMetadata.setEditableSchemaFieldInfo(
                    new EditableSchemaFieldInfoArray(
                            datasourceUpdateInput.getEditableSchemaMetadata().getEditableSchemaFieldInfo().stream().map(
                                    element -> mapSchemaFieldInfo(element)
                            ).collect(Collectors.toList())));
            result.setEditableSchemaMetadata(editableSchemaMetadata);

        }

        if (datasourceUpdateInput.getEditableProperties() != null) {
            final EditableDatasourceProperties editableDatasourceProperties = new EditableDatasourceProperties();
            editableDatasourceProperties.setDescription(datasourceUpdateInput.getEditableProperties().getDescription());
            result.setEditableProperties(editableDatasourceProperties);
        }

        return result;
    }

    private EditableSchemaFieldInfo mapSchemaFieldInfo(
            final com.linkedin.datahub.graphql.generated.EditableSchemaFieldInfoUpdate schemaFieldInfo
    ) {
        final EditableSchemaFieldInfo output = new EditableSchemaFieldInfo();

        if (schemaFieldInfo.getDescription() != null) {
            output.setDescription(schemaFieldInfo.getDescription());
        }
        output.setFieldPath(schemaFieldInfo.getFieldPath());

        if (schemaFieldInfo.getGlobalTags() != null) {
            final GlobalTags globalTags = new GlobalTags();
            globalTags.setTags(new TagAssociationArray(schemaFieldInfo.getGlobalTags().getTags().stream().map(
                    element -> TagAssociationUpdateMapper.map(element)).collect(Collectors.toList())));
            output.setGlobalTags(globalTags);
        }

        return output;
    }
}
