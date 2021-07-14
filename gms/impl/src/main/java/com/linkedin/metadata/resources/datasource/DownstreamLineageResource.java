package com.linkedin.metadata.resources.datasource;

import com.google.common.collect.ImmutableList;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.datasource.DatasourceDownstreamLineage;
import com.linkedin.datasource.DatasourceKey;
import com.linkedin.datasource.DatasourceDownstream;
import com.linkedin.datasource.DatasourceDownstreamArray;
import com.linkedin.datasource.DatasourceUpstream;
import com.linkedin.datasource.DSUpstreamLineage;
import com.linkedin.metadata.PegasusUtils;
import com.linkedin.metadata.entity.EntityService;
import com.linkedin.metadata.graph.GraphService;
import com.linkedin.metadata.query.CriterionArray;
import com.linkedin.metadata.query.Filter;
import com.linkedin.metadata.query.RelationshipDirection;
import com.linkedin.metadata.restli.RestliUtils;
import com.linkedin.parseq.Task;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.server.PathKeys;
import com.linkedin.restli.server.annotations.PathKeysParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.annotations.RestLiSimpleResource;
import com.linkedin.restli.server.annotations.RestMethod;
import com.linkedin.restli.server.resources.SimpleResourceTemplate;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.linkedin.metadata.dao.Neo4jUtil.createRelationshipFilter;
import static com.linkedin.metadata.dao.utils.QueryUtils.newFilter;


/**
 * Deprecated! Use {@link EntityResource} instead.
 *
 * Rest.li entry point: /datasources/{datasourceKey}/downstreamLineage
 */
@Deprecated
@RestLiSimpleResource(name = "downstreamLineage", namespace = "com.linkedin.datasource", parent = Datasources.class)
public final class DownstreamLineageResource extends SimpleResourceTemplate<DatasourceDownstreamLineage> {

  private static final String DATASET_KEY = Datasources.class.getAnnotation(RestLiCollection.class).keyName();
  private static final Filter EMPTY_FILTER = new Filter().setCriteria(new CriterionArray());
  private static final Integer MAX_DOWNSTREAM_CNT = 100;

  @Inject
  @Named("entityService")
  private EntityService _entityService;

  @Inject
  @Named("graphService")
  private GraphService _graphService;

  public DownstreamLineageResource() {
    super();
  }

  @Nonnull
  @RestMethod.Get
  public Task<DatasourceDownstreamLineage> get(@PathKeysParam @Nonnull PathKeys keys) {
    final DatasourceUrn datasourceUrn = getUrn(keys);

    return RestliUtils.toTask(() -> {

      final List<DatasourceUrn> downstreamUrns = _graphService.findRelatedUrns(
          "datasource",
          newFilter("urn", datasourceUrn.toString()),
          "datasource",
          EMPTY_FILTER,
          ImmutableList.of("DownstreamOf"),
          createRelationshipFilter(EMPTY_FILTER, RelationshipDirection.INCOMING),
          0,
          MAX_DOWNSTREAM_CNT
      ).stream().map(urnStr -> {
        try {
          return DatasourceUrn.createFromString(urnStr);
        } catch (URISyntaxException e) {
          throw new RuntimeException(String.format("Failed to convert urn in Neo4j to Urn type %s", urnStr));
        }
      }).collect(Collectors.toList());

      final DatasourceDownstreamArray downstreamArray = new DatasourceDownstreamArray(downstreamUrns.stream()
          .map(ds -> {
            final RecordTemplate upstreamLineageRecord =
                _entityService.getLatestAspect(
                    ds,
                    PegasusUtils.getAspectNameFromSchema(new DSUpstreamLineage().schema())
                );
            if (upstreamLineageRecord != null) {
              final DSUpstreamLineage upstreamLineage = new DSUpstreamLineage(upstreamLineageRecord.data());
              final List<DatasourceUpstream> upstreams = upstreamLineage.getUpstreams().stream()
                  .filter(us -> us.getDatasource().equals(datasourceUrn))
                  .collect(Collectors.toList());
              if (upstreams.size() != 1) {
                throw new RuntimeException(String.format("There is no relation or more than 1 relation between the datasources!"));
              }
              return new DatasourceDownstream()
                  .setDatasource(ds)
                  .setType(upstreams.get(0).getType())
                  .setAuditStamp(upstreams.get(0).getAuditStamp());
            }
            return null;
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toList())
      );
      return new DatasourceDownstreamLineage().setDownstreams(downstreamArray);
    });
  }

  @Nonnull
  private DatasourceUrn getUrn(@PathKeysParam @Nonnull PathKeys keys) {
    DatasourceKey key = keys.<ComplexResourceKey<DatasourceKey, EmptyRecord>>get(DATASET_KEY).getKey();
    return new DatasourceUrn(key.getPlatform(), key.getName(), key.getOrigin());
  }
}
