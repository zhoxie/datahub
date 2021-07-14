package com.linkedin.datahub.graphql.types.datasource;

import com.google.common.collect.ImmutableSet;
import com.linkedin.common.AuditStamp;
import com.linkedin.common.urn.CorpuserUrn;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.template.SetMode;
import com.linkedin.data.template.StringArray;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.AutoCompleteResults;
import com.linkedin.datahub.graphql.generated.BrowsePath;
import com.linkedin.datahub.graphql.generated.BrowseResults;
import com.linkedin.datahub.graphql.generated.Datasource;
import com.linkedin.datahub.graphql.generated.DatasourceUpdateInput;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.generated.FacetFilterInput;
import com.linkedin.datahub.graphql.generated.SearchResults;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datahub.graphql.types.BrowsableEntityType;
import com.linkedin.datahub.graphql.types.MutableType;
import com.linkedin.datahub.graphql.types.SearchableEntityType;
import com.linkedin.datahub.graphql.types.datasource.mappers.DatasourceSnapshotMapper;
import com.linkedin.datahub.graphql.types.datasource.mappers.DatasourceUpdateInputMapper;
import com.linkedin.datahub.graphql.types.mappers.AutoCompleteResultsMapper;
import com.linkedin.datahub.graphql.types.mappers.BrowsePathsMapper;
import com.linkedin.datahub.graphql.types.mappers.BrowseResultMetadataMapper;
import com.linkedin.datahub.graphql.types.mappers.UrnSearchResultsMapper;
import com.linkedin.datasource.client.Datasources;
import com.linkedin.entity.Entity;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.extractor.SnapshotToAspectMap;
import com.linkedin.metadata.query.AutoCompleteResult;
import com.linkedin.metadata.query.BrowseResult;
import com.linkedin.metadata.query.SearchResult;
import com.linkedin.metadata.snapshot.Snapshot;
import com.linkedin.r2.RemoteInvocationException;
import graphql.execution.DataFetcherResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.linkedin.datahub.graphql.Constants.BROWSE_PATH_DELIMITER;

public class DatasourceType implements SearchableEntityType<Datasource>, BrowsableEntityType<Datasource>, MutableType<DatasourceUpdateInput> {

    private static final Set<String> FACET_FIELDS = ImmutableSet.of("origin", "platform");

    private final EntityClient _datasourcesClient;

    public DatasourceType(final EntityClient datasourcesClient) {
        _datasourcesClient = datasourcesClient;
    }

    @Override
    public Class<Datasource> objectClass() {
        return Datasource.class;
    }

    @Override
    public Class<DatasourceUpdateInput> inputClass() {
        return DatasourceUpdateInput.class;
    }

    @Override
    public EntityType type() {
        return EntityType.DATASOURCE;
    }

    @Override
    public List<DataFetcherResult<Datasource>> batchLoad(final List<String> urns, final QueryContext context) {

        final List<DatasourceUrn> datasourceUrns = urns.stream()
                .map(DatasourceUtils::getDatasourceUrn)
                .collect(Collectors.toList());

        try {
            final Map<Urn, Entity> datasourceMap = _datasourcesClient.batchGet(datasourceUrns
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));

            final List<Entity> gmsResults = new ArrayList<>();
            for (DatasourceUrn urn : datasourceUrns) {
                gmsResults.add(datasourceMap.getOrDefault(urn, null));
            }
            return gmsResults.stream()
                .map(gmsDatasource ->
                    gmsDatasource == null ? null : DataFetcherResult.<Datasource>newResult()
                        .data(DatasourceSnapshotMapper.map(gmsDatasource.getValue().getDatasourceSnapshot()))
                        .localContext(SnapshotToAspectMap.extractAspectMap(gmsDatasource.getValue().getDatasourceSnapshot()))
                        .build()
                )
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to batch load Datasources", e);
        }
    }

    @Override
    public SearchResults search(@Nonnull String query,
                                @Nullable List<FacetFilterInput> filters,
                                int start,
                                int count,
                                @Nonnull final QueryContext context) throws Exception {
        final Map<String, String> facetFilters = ResolverUtils.buildFacetFilters(filters, FACET_FIELDS);
        final SearchResult searchResult = _datasourcesClient.search("datasource", query, facetFilters, start, count);
        return UrnSearchResultsMapper.map(searchResult);
    }

    @Override
    public AutoCompleteResults autoComplete(@Nonnull String query,
                                            @Nullable String field,
                                            @Nullable List<FacetFilterInput> filters,
                                            int limit,
                                            @Nonnull final QueryContext context) throws Exception {
        final Map<String, String> facetFilters = ResolverUtils.buildFacetFilters(filters, FACET_FIELDS);
        final AutoCompleteResult result = _datasourcesClient.autoComplete("datasource", query, facetFilters, limit);
        return AutoCompleteResultsMapper.map(result);
    }

    @Override
    public BrowseResults browse(@Nonnull List<String> path,
                                @Nullable List<FacetFilterInput> filters,
                                int start,
                                int count,
                                @Nonnull final QueryContext context) throws Exception {
        final Map<String, String> facetFilters = ResolverUtils.buildFacetFilters(filters, FACET_FIELDS);
        final String pathStr = path.size() > 0 ? BROWSE_PATH_DELIMITER + String.join(BROWSE_PATH_DELIMITER, path) : "";
        final BrowseResult result = _datasourcesClient.browse(
                "datasource",
                pathStr,
                facetFilters,
                start,
                count);
        final List<String> urns = result.getEntities().stream().map(entity -> entity.getUrn().toString()).collect(Collectors.toList());
        final List<Datasource> datasources = batchLoad(urns, context)
            .stream().map(datasourceDataFetcherResult -> datasourceDataFetcherResult.getData()).collect(Collectors.toList());
        final BrowseResults browseResults = new BrowseResults();
        browseResults.setStart(result.getFrom());
        browseResults.setCount(result.getPageSize());
        browseResults.setTotal(result.getNumEntities());
        browseResults.setMetadata(BrowseResultMetadataMapper.map(result.getMetadata()));
        browseResults.setEntities(datasources.stream()
                .map(datasource -> (com.linkedin.datahub.graphql.generated.Entity) datasource)
                .collect(Collectors.toList()));
        return browseResults;
    }

    @Override
    public List<BrowsePath> browsePaths(@Nonnull String urn, @Nonnull final QueryContext context) throws Exception {
        final StringArray result = _datasourcesClient.getBrowsePaths(DatasourceUtils.getDatasourceUrn(urn));
        return BrowsePathsMapper.map(result);
    }

    @Override
    public Datasource update(@Nonnull DatasourceUpdateInput input, @Nonnull QueryContext context) throws Exception {
        // TODO: Verify that updater is owner.
        final CorpuserUrn actor = CorpuserUrn.createFromString(context.getActor());
        final com.linkedin.datasource.Datasource partialDatasource = DatasourceUpdateInputMapper.map(input, actor);
        partialDatasource.setUrn(DatasourceUrn.createFromString(input.getUrn()));


        // TODO: Migrate inner mappers to InputModelMappers & remove
        // Create Audit Stamp
        final AuditStamp auditStamp = new AuditStamp();
        auditStamp.setActor(actor, SetMode.IGNORE_NULL);
        auditStamp.setTime(System.currentTimeMillis());

        if (partialDatasource.hasDeprecation()) {
            partialDatasource.getDeprecation().setActor(actor, SetMode.IGNORE_NULL);
        }

        if (partialDatasource.hasEditableSchemaMetadata()) {
            partialDatasource.getEditableSchemaMetadata().setLastModified(auditStamp);
            if (!partialDatasource.getEditableSchemaMetadata().hasCreated()) {
                partialDatasource.getEditableSchemaMetadata().setCreated(auditStamp);
            }
        }

        if (partialDatasource.hasEditableProperties()) {
            partialDatasource.getEditableProperties().setLastModified(auditStamp);
            if (!partialDatasource.getEditableProperties().hasCreated()) {
                partialDatasource.getEditableProperties().setCreated(auditStamp);
            }
        }

        partialDatasource.setLastModified(auditStamp);

        try {
            Entity entity = new Entity();
            entity.setValue(Snapshot.create(Datasources.toSnapshot(partialDatasource.getUrn(), partialDatasource)));
            _datasourcesClient.update(entity);
        } catch (RemoteInvocationException e) {
            throw new RuntimeException(String.format("Failed to write entity with urn %s", input.getUrn()), e);
        }

        return load(input.getUrn(), context).getData();
    }
}
