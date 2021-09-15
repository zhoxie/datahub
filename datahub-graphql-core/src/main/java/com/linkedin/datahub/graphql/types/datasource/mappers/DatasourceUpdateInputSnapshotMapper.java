package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.common.AuditStamp;
import com.linkedin.common.GlobalTags;
import com.linkedin.common.TagAssociationArray;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.template.SetMode;
import com.linkedin.datahub.graphql.generated.DatasourceUpdateInput;
import com.linkedin.datahub.graphql.types.common.mappers.InstitutionalMemoryUpdateMapper;
import com.linkedin.datahub.graphql.types.common.mappers.OwnershipUpdateMapper;
import com.linkedin.datahub.graphql.types.mappers.InputModelMapper;
import com.linkedin.datahub.graphql.types.tag.mappers.TagAssociationUpdateMapper;
import com.linkedin.datasource.DatasourceDeprecation;
import com.linkedin.datasource.EditableDatasourceProperties;
import com.linkedin.metadata.aspect.DatasourceAspect;
import com.linkedin.metadata.aspect.DatasourceAspectArray;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class DatasourceUpdateInputSnapshotMapper implements InputModelMapper<DatasourceUpdateInput, DatasourceSnapshot, Urn> {

  public static final DatasourceUpdateInputSnapshotMapper INSTANCE = new DatasourceUpdateInputSnapshotMapper();

  public static DatasourceSnapshot map(
      @Nonnull final DatasourceUpdateInput datasourceUpdateInput,
      @Nonnull final Urn actor) {
    return INSTANCE.apply(datasourceUpdateInput, actor);
  }

  @Override
  public DatasourceSnapshot apply(
      @Nonnull final DatasourceUpdateInput datasourceUpdateInput,
      @Nonnull final Urn actor) {
    final DatasourceSnapshot result = new DatasourceSnapshot();
    final AuditStamp auditStamp = new AuditStamp();
    auditStamp.setActor(actor, SetMode.IGNORE_NULL);
    auditStamp.setTime(System.currentTimeMillis());

    try {
      result.setUrn(DatasourceUrn.createFromString(datasourceUpdateInput.getUrn()));
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(
          String.format("Failed to validate provided urn with value %s", datasourceUpdateInput.getUrn()));
    }

    final DatasourceAspectArray aspects = new DatasourceAspectArray();

    if (datasourceUpdateInput.getOwnership() != null) {
      aspects.add(DatasourceAspect.create(
          OwnershipUpdateMapper.map(datasourceUpdateInput.getOwnership(), actor)));
    }

    if (datasourceUpdateInput.getDeprecation() != null) {
      final DatasourceDeprecation deprecation = new DatasourceDeprecation();
      deprecation.setDeprecated(datasourceUpdateInput.getDeprecation().getDeprecated());
      if (datasourceUpdateInput.getDeprecation().getDecommissionTime() != null) {
        deprecation.setDecommissionTime(datasourceUpdateInput.getDeprecation().getDecommissionTime());
      }
      deprecation.setNote(datasourceUpdateInput.getDeprecation().getNote());
      deprecation.setActor(actor, SetMode.IGNORE_NULL);
      aspects.add(DatasourceAspect.create(deprecation));
    }

    if (datasourceUpdateInput.getInstitutionalMemory() != null) {
      aspects.add(DatasourceAspect.create(InstitutionalMemoryUpdateMapper.map(datasourceUpdateInput.getInstitutionalMemory())));
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
      aspects.add(DatasourceAspect.create(globalTags));
    }

    if (datasourceUpdateInput.getEditableProperties() != null) {
      final EditableDatasourceProperties editableDatasetProperties = new EditableDatasourceProperties();
      editableDatasetProperties.setDescription(datasourceUpdateInput.getEditableProperties().getDescription());
      editableDatasetProperties.setLastModified(auditStamp);
      editableDatasetProperties.setCreated(auditStamp);
      aspects.add(DatasourceAspect.create(editableDatasetProperties));
    }

    result.setAspects(aspects);

    return result;
  }
}