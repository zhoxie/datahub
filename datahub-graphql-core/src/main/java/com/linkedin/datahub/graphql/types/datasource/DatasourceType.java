package com.linkedin.datahub.graphql.types.datasource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.linkedin.common.urn.CorpuserUrn;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.template.StringArray;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.authorization.AuthorizationUtils;
import com.linkedin.datahub.graphql.authorization.ConjunctivePrivilegeGroup;
import com.linkedin.datahub.graphql.authorization.DisjunctivePrivilegeGroup;
import com.linkedin.datahub.graphql.exception.AuthorizationException;
import com.linkedin.datahub.graphql.generated.Datasource;
import com.linkedin.datahub.graphql.generated.DatasourceUpdateInput;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.generated.SearchResults;
import com.linkedin.datahub.graphql.generated.FacetFilterInput;
import com.linkedin.datahub.graphql.generated.AutoCompleteResults;
import com.linkedin.datahub.graphql.generated.BrowsePath;
import com.linkedin.datahub.graphql.generated.BrowseResults;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datahub.graphql.types.BrowsableEntityType;
import com.linkedin.datahub.graphql.types.MutableType;
import com.linkedin.datahub.graphql.types.SearchableEntityType;
import com.linkedin.datahub.graphql.types.datasource.mappers.DatasourceSnapshotMapper;
import com.linkedin.datahub.graphql.types.datasource.mappers.DatasourceUpdateInputSnapshotMapper;
import com.linkedin.datahub.graphql.types.mappers.AutoCompleteResultsMapper;
import com.linkedin.datahub.graphql.types.mappers.BrowsePathsMapper;
import com.linkedin.datahub.graphql.types.mappers.BrowseResultMapper;
import com.linkedin.datahub.graphql.types.mappers.UrnSearchResultsMapper;
import com.linkedin.entity.Entity;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.authorization.PoliciesConfig;
import com.linkedin.metadata.browse.BrowseResult;
import com.linkedin.metadata.extractor.AspectExtractor;
import com.linkedin.metadata.query.AutoCompleteResult;
import com.linkedin.metadata.search.SearchResult;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;
import com.linkedin.metadata.snapshot.Snapshot;
import com.linkedin.r2.RemoteInvocationException;
import graphql.execution.DataFetcherResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.linkedin.datahub.graphql.Constants.BROWSE_PATH_DELIMITER;

public class DatasourceType implements SearchableEntityType<Datasource>, BrowsableEntityType<Datasource>,
                                    MutableType<DatasourceUpdateInput, Datasource> {

    private static final Set<String> FACET_FIELDS = ImmutableSet.of("origin", "platform");
    private static final String ENTITY_NAME = "datasource";

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
                        .collect(Collectors.toSet()),
                context.getAuthentication());

            final List<Entity> gmsResults = new ArrayList<>();
            for (DatasourceUrn urn : datasourceUrns) {
                gmsResults.add(datasourceMap.getOrDefault(urn, null));
            }
            return gmsResults.stream()
                .map(gmsDatasource ->
                    gmsDatasource == null ? null : DataFetcherResult.<Datasource>newResult()
                        .data(DatasourceSnapshotMapper.map(gmsDatasource.getValue().getDatasourceSnapshot()))
                        .localContext(AspectExtractor.extractAspects(gmsDatasource.getValue().getDatasourceSnapshot()))
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
        final SearchResult searchResult = _datasourcesClient.search(ENTITY_NAME, query, facetFilters, start, count, context.getAuthentication());
        return UrnSearchResultsMapper.map(searchResult);
    }

    @Override
    public AutoCompleteResults autoComplete(@Nonnull String query,
                                            @Nullable String field,
                                            @Nullable List<FacetFilterInput> filters,
                                            int limit,
                                            @Nonnull final QueryContext context) throws Exception {
        final Map<String, String> facetFilters = ResolverUtils.buildFacetFilters(filters, FACET_FIELDS);
        final AutoCompleteResult result = _datasourcesClient.autoComplete(ENTITY_NAME, query, facetFilters, limit, context.getAuthentication());
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
                ENTITY_NAME,
                pathStr,
                facetFilters,
                start,
                count,
                context.getAuthentication());
        return BrowseResultMapper.map(result);
    }

    @Override
    public List<BrowsePath> browsePaths(@Nonnull String urn, @Nonnull final QueryContext context) throws Exception {
        final StringArray result = _datasourcesClient.getBrowsePaths(DatasourceUtils.getDatasourceUrn(urn), context.getAuthentication());
        return BrowsePathsMapper.map(result);
    }

    @Override
    public Datasource update(@Nonnull String urn, @Nonnull DatasourceUpdateInput input, @Nonnull QueryContext context) throws Exception {
        if (isAuthorized(urn, input, context)) {
            final CorpuserUrn actor = CorpuserUrn.createFromString(context.getAuthentication().getActor().toUrnStr());
            final DatasourceSnapshot datasourceSnapshot = DatasourceUpdateInputSnapshotMapper.map(input, actor);
            datasourceSnapshot.setUrn(DatasourceUrn.createFromString(urn));
            final Snapshot snapshot = Snapshot.create(datasourceSnapshot);

            try {
                Entity entity = new Entity();
                entity.setValue(snapshot);
                _datasourcesClient.update(entity, context.getAuthentication());
            } catch (RemoteInvocationException e) {
                throw new RuntimeException(String.format("Failed to write entity with urn %s", urn), e);
            }

            return load(urn, context).getData();
        }
        throw new AuthorizationException("Unauthorized to perform this action. Please contact your DataHub administrator.");
    }

    private boolean isAuthorized(@Nonnull String urn, @Nonnull DatasourceUpdateInput update, @Nonnull QueryContext context) {
        // Decide whether the current principal should be allowed to update the Dataset.
        final DisjunctivePrivilegeGroup orPrivilegeGroups = getAuthorizedPrivileges(update);
        return AuthorizationUtils.isAuthorized(
                context.getAuthorizer(),
                context.getAuthentication().getActor().toUrnStr(),
                PoliciesConfig.DATASOURCE_PRIVILEGES.getResourceType(),
                urn,
                orPrivilegeGroups);
    }

    private DisjunctivePrivilegeGroup getAuthorizedPrivileges(final DatasourceUpdateInput updateInput) {

        final ConjunctivePrivilegeGroup allPrivilegesGroup = new ConjunctivePrivilegeGroup(ImmutableList.of(
                PoliciesConfig.EDIT_ENTITY_PRIVILEGE.getType()
        ));

        List<String> specificPrivileges = new ArrayList<>();
        if (updateInput.getInstitutionalMemory() != null) {
            specificPrivileges.add(PoliciesConfig.EDIT_ENTITY_DOC_LINKS_PRIVILEGE.getType());
        }
        if (updateInput.getOwnership() != null) {
            specificPrivileges.add(PoliciesConfig.EDIT_ENTITY_OWNERS_PRIVILEGE.getType());
        }
        if (updateInput.getDeprecation() != null) {
            specificPrivileges.add(PoliciesConfig.EDIT_ENTITY_STATUS_PRIVILEGE.getType());
        }
        if (updateInput.getEditableProperties() != null) {
            specificPrivileges.add(PoliciesConfig.EDIT_ENTITY_DOCS_PRIVILEGE.getType());
        }
        if (updateInput.getGlobalTags() != null) {
            specificPrivileges.add(PoliciesConfig.EDIT_ENTITY_TAGS_PRIVILEGE.getType());
        }

        final ConjunctivePrivilegeGroup specificPrivilegeGroup = new ConjunctivePrivilegeGroup(specificPrivileges);

        // If you either have all entity privileges, or have the specific privileges required, you are authorized.
        return new DisjunctivePrivilegeGroup(ImmutableList.of(
                allPrivilegesGroup,
                specificPrivilegeGroup
        ));
    }
}
