package com.linkedin.metadata.builders.graph;

import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.metadata.builders.graph.relationship.BaseRelationshipBuilder;
import com.linkedin.metadata.builders.graph.relationship.DownstreamOfBuilderFromUpstreamLineage;
import com.linkedin.metadata.builders.graph.relationship.OwnedByBuilderFromOwnership;
import com.linkedin.metadata.entity.DatasourceEntity;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatasourceGraphBuilder extends BaseGraphBuilder<DatasourceSnapshot> {
  private static final Set<BaseRelationshipBuilder> RELATIONSHIP_BUILDERS =
      Collections.unmodifiableSet(new HashSet<BaseRelationshipBuilder>() {
        {
          add(new DownstreamOfBuilderFromUpstreamLineage());
          add(new OwnedByBuilderFromOwnership());
        }
      });

  public DatasourceGraphBuilder() {
    super(DatasourceSnapshot.class, RELATIONSHIP_BUILDERS);
  }

  @Nonnull
  @Override
  protected List<? extends RecordTemplate> buildEntities(@Nonnull DatasourceSnapshot snapshot) {
    final DatasourceUrn urn = snapshot.getUrn();
    final DatasourceEntity entity = new DatasourceEntity().setUrn(urn)
        .setName(urn.getDatasourceNameEntity())
        .setCategory(urn.getCategoryEntity())
        .setOrigin(urn.getOriginEntity());

    setRemovedProperty(snapshot, entity);

    return Collections.singletonList(entity);
  }
}
