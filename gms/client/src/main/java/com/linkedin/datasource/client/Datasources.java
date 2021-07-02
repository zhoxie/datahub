package com.linkedin.datasource.client;

import com.linkedin.BatchGetUtils;
import com.linkedin.common.Status;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.template.StringArray;
import com.linkedin.datasource.Datasource;
import com.linkedin.datasource.DatasourceKey;
import com.linkedin.datasource.DatasourceProperties;
import com.linkedin.datasource.DatasourcesDoAutocompleteRequestBuilder;
import com.linkedin.datasource.DatasourcesDoBrowseRequestBuilder;
import com.linkedin.datasource.DatasourcesDoGetBrowsePathsRequestBuilder;
import com.linkedin.datasource.DatasourcesDoGetSnapshotRequestBuilder;
import com.linkedin.datasource.DatasourcesFindByFilterRequestBuilder;
import com.linkedin.datasource.DatasourcesFindBySearchRequestBuilder;
import com.linkedin.datasource.DatasourcesRequestBuilders;
import com.linkedin.metadata.aspect.DatasourceAspect;
import com.linkedin.metadata.dao.DatasourceActionRequestBuilder;
import com.linkedin.metadata.dao.utils.ModelUtils;
import com.linkedin.metadata.query.AutoCompleteResult;
import com.linkedin.metadata.query.BrowseResult;
import com.linkedin.metadata.query.IndexFilter;
import com.linkedin.metadata.query.SortCriterion;
import com.linkedin.metadata.restli.BaseBrowsableClient;
import com.linkedin.metadata.snapshot.DatasourceSnapshot;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.linkedin.metadata.dao.utils.QueryUtils.newFilter;

public class Datasources extends BaseBrowsableClient<Datasource, DatasourceUrn> {
    private static final DatasourcesRequestBuilders DATASETS_REQUEST_BUILDERS = new DatasourcesRequestBuilders();
    private static final DatasourceActionRequestBuilder DATASET_ACTION_REQUEST_BUILDERS = new DatasourceActionRequestBuilder();

    public Datasources(@Nonnull Client restliClient) {
        super(restliClient);
    }

    /**
     * Gets {@link Datasource} model for the given urn
     *
     * @param urn datasource urn
     * @return {@link Datasource} datasource model
     * @throws RemoteInvocationException
     */
    @Nonnull
    public Datasource get(@Nonnull DatasourceUrn urn)
            throws RemoteInvocationException {
        GetRequest<Datasource> getRequest = DATASETS_REQUEST_BUILDERS.get()
                .id(new ComplexResourceKey<>(toDatasourceKey(urn), new EmptyRecord()))
                .build();

        return _client.sendRequest(getRequest).getResponse().getEntity();
    }

    /**
     * Searches for datasources matching to a given query and filters
     *
     * @param input search query
     * @param requestFilters search filters
     * @param start start offset for search results
     * @param count max number of search results requested
     * @return Snapshot key
     * @throws RemoteInvocationException
     */
    @Nonnull
    public CollectionResponse<Datasource> search(@Nonnull String input, @Nonnull Map<String, String> requestFilters,
                                               int start, int count) throws RemoteInvocationException {

        return search(input, null, requestFilters, null, start, count);
    }

    @Override
    @Nonnull
    public CollectionResponse<Datasource> search(@Nonnull String input, @Nullable StringArray aspectNames,
                                                 @Nullable Map<String, String> requestFilters, @Nullable SortCriterion sortCriterion, int start, int count)
        throws RemoteInvocationException {

        final DatasourcesFindBySearchRequestBuilder requestBuilder = DATASETS_REQUEST_BUILDERS.findBySearch()
            .inputParam(input)
            .aspectsParam(aspectNames)
            .filterParam(newFilter(requestFilters))
            .sortParam(sortCriterion)
            .paginate(start, count);
        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    /**
     * Gets browse snapshot of a given path
     *
     * @param query search query
     * @param field field of the datasource
     * @param requestFilters autocomplete filters
     * @param limit max number of autocomplete results
     * @throws RemoteInvocationException
     */
    @Nonnull
    public AutoCompleteResult autoComplete(@Nonnull String query, @Nonnull String field,
                                           @Nonnull Map<String, String> requestFilters,
                                           @Nonnull int limit) throws RemoteInvocationException {
        DatasourcesDoAutocompleteRequestBuilder requestBuilder = DATASETS_REQUEST_BUILDERS
                .actionAutocomplete()
                .queryParam(query)
                .fieldParam(field)
                .filterParam(newFilter(requestFilters))
                .limitParam(limit);
        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    /**
     * Gets browse snapshot of a given path
     *
     * @param path path being browsed
     * @param requestFilters browse filters
     * @param start start offset of first datasource
     * @param limit max number of datasources
     * @throws RemoteInvocationException
     */
    @Nonnull
    @Override
    public BrowseResult browse(@Nonnull String path, @Nullable Map<String, String> requestFilters,
                               int start, int limit) throws RemoteInvocationException {
        DatasourcesDoBrowseRequestBuilder requestBuilder = DATASETS_REQUEST_BUILDERS
                .actionBrowse()
                .pathParam(path)
                .startParam(start)
                .limitParam(limit);
        if (requestFilters != null) {
            requestBuilder.filterParam(newFilter(requestFilters));
        }
        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    /**
     * Gets list of datasource urns from strongly consistent local secondary index
     *
     * @param lastUrn last datasource urn of the previous fetched page. For the first page, this will be NULL
     * @param size size of the page that needs to be fetched
     * @return list of datasource urns represented as {@link StringArray}
     * @throws RemoteInvocationException
     */
    @Nonnull
    public List<String> listUrnsFromIndex(@Nullable DatasourceUrn lastUrn, int size) throws RemoteInvocationException {
        return listUrnsFromIndex(null, lastUrn, size);
    }

    /**
     * Gets list of datasource urns from strongly consistent local secondary index given an {@link IndexFilter} specifying filter conditions
     *
     * @param indexFilter index filter that defines the filter conditions
     * @param lastUrn last datasource urn of the previous fetched page. For the first page, this will be NULL
     * @param size size of the page that needs to be fetched
     * @return list of datasource urns represented as {@link StringArray}
     * @throws RemoteInvocationException
     */
    @Nonnull
    public List<String> listUrnsFromIndex(@Nullable IndexFilter indexFilter, @Nullable DatasourceUrn lastUrn, int size)
        throws RemoteInvocationException {
        final List<Datasource> response = filter(indexFilter, Collections.emptyList(), lastUrn, size);
        return response.stream()
            .map(datasource -> new DatasourceUrn(datasource.getPlatform(), datasource.getName(), datasource.getOrigin()))
            .map(Urn::toString)
            .collect(Collectors.toList());
    }

    /**
     * Gets a list of {@link Datasource} whose raw metadata contains the list of datasource urns from strongly consistent
     * local secondary index that satisfy the filter conditions provided in {@link IndexFilter}
     *
     * @param indexFilter {@link IndexFilter} that specifies the filter conditions for urns to be fetched from secondary index
     * @param aspectNames list of aspects whose value should be retrieved
     * @param lastUrn last datasource urn of the previous fetched page. For the first page, this will be NULL
     * @param size size of the page that needs to be fetched
     * @return collection of {@link Datasource} whose raw metadata contains the list of filtered datasource urns
     * @throws RemoteInvocationException
     */
    @Nonnull
    public List<Datasource> filter(@Nullable IndexFilter indexFilter, @Nullable List<String> aspectNames,
        @Nullable DatasourceUrn lastUrn, int size) throws RemoteInvocationException {
        final DatasourcesFindByFilterRequestBuilder requestBuilder =
            DATASETS_REQUEST_BUILDERS.findByFilter().filterParam(indexFilter).aspectsParam(aspectNames).paginate(0, size);
        if (lastUrn != null) {
            requestBuilder.urnParam(lastUrn.toString());
        }
        return _client.sendRequest(requestBuilder.build()).getResponseEntity().getElements();
    }

    /**
     * Gets browse path(s) given datasource urn
     *
     * @param urn urn for the entity
     * @return list of paths given urn
     * @throws RemoteInvocationException
     */
    @Nonnull
    public StringArray getBrowsePaths(@Nonnull DatasourceUrn urn) throws RemoteInvocationException {
        DatasourcesDoGetBrowsePathsRequestBuilder requestBuilder = DATASETS_REQUEST_BUILDERS
                .actionGetBrowsePaths()
                .urnParam(urn);
        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    /**
     * Batch gets list of {@link Datasource} models
     *
     * @param urns list of datasource urn
     * @return map of {@link Datasource} models
     * @throws RemoteInvocationException
     */
    @Nonnull
    public Map<DatasourceUrn, Datasource> batchGet(@Nonnull Set<DatasourceUrn> urns)
        throws RemoteInvocationException {
        return BatchGetUtils.batchGet(
                urns,
                (Void v) -> DATASETS_REQUEST_BUILDERS.batchGet(),
                this::getKeyFromUrn,
                this::getUrnFromKey,
                _client
        );
    }

    /**
     * Gets latest full datasource snapshot given datasource urn
     *
     * @param datasourceUrn datasource urn
     * @return latest full datasource snapshot
     * @throws RemoteInvocationException
     */
    public DatasourceSnapshot getLatestFullSnapshot(@Nonnull DatasourceUrn datasourceUrn) throws RemoteInvocationException {
        DatasourcesDoGetSnapshotRequestBuilder requestBuilder = DATASETS_REQUEST_BUILDERS
                .actionGetSnapshot()
                .urnParam(datasourceUrn.toString());
        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    @Nonnull
    private ComplexResourceKey<DatasourceKey, EmptyRecord> getKeyFromUrn(@Nonnull DatasourceUrn urn) {
        return new ComplexResourceKey<>(toDatasourceKey(urn), new EmptyRecord());
    }

    @Nonnull
    private DatasourceUrn getUrnFromKey(@Nonnull ComplexResourceKey<DatasourceKey, EmptyRecord> key) {
        return toDatasourceUrn(key.getKey());
    }

    @Nonnull
    private DatasourceKey toDatasourceKey(@Nonnull DatasourceUrn urn) {
        return new DatasourceKey()
            .setName(urn.getDatasourceNameEntity())
            .setOrigin(urn.getOriginEntity())
            .setPlatform(urn.getPlatformEntity());
    }

    @Nonnull
    private DatasourceUrn toDatasourceUrn(@Nonnull DatasourceKey key) {
        return new DatasourceUrn(key.getPlatform(), key.getName(), key.getOrigin());
    }

    /**
     * Update an existing Datasource
     */
    public void update(@Nonnull final DatasourceUrn urn, @Nonnull final Datasource datasource) throws RemoteInvocationException {
        Request request = DATASET_ACTION_REQUEST_BUILDERS.createRequest(urn, toSnapshot(urn, datasource));
        _client.sendRequest(request).getResponse();
    }

    // Copied from an unused method in Datasources resource.
    public static DatasourceSnapshot toSnapshot(@Nonnull final DatasourceUrn datasourceUrn, @Nonnull final Datasource datasource) {
        final List<DatasourceAspect> aspects = new ArrayList<>();
        if (datasource.getProperties() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, getDatasourcePropertiesAspect(datasource)));
        }
        if (datasource.getEditableProperties() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getEditableProperties()));
        }
        if (datasource.getDeprecation() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getDeprecation()));
        }
        if (datasource.getInstitutionalMemory() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getInstitutionalMemory()));
        }
        if (datasource.getOwnership() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getOwnership()));
        }
        if (datasource.getSchemaMetadata() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getSchemaMetadata()));
        }
        if (datasource.getStatus() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getStatus()));
        }
        if (datasource.hasRemoved()) {
            aspects.add(DatasourceAspect.create(new Status().setRemoved(datasource.isRemoved())));
        }
        if (datasource.getGlobalTags() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getGlobalTags()));
        }
        if (datasource.getEditableSchemaMetadata() != null) {
            aspects.add(ModelUtils.newAspectUnion(DatasourceAspect.class, datasource.getEditableSchemaMetadata()));
        }
        return ModelUtils.newSnapshot(DatasourceSnapshot.class, datasourceUrn, aspects);
    }

    private static DatasourceProperties getDatasourcePropertiesAspect(@Nonnull Datasource datasource) {
        final DatasourceProperties datasourceProperties = new DatasourceProperties();
        datasourceProperties.setDescription(datasource.getDescription());
        datasourceProperties.setTags(datasource.getTags());
        if (datasource.getUri() != null)  {
            datasourceProperties.setUri(datasource.getUri());
        }
        if (datasource.getProperties() != null) {
            datasourceProperties.setCustomProperties(datasource.getProperties());
        }
        if (datasource.getExternalUrl() != null) {
            datasourceProperties.setExternalUrl(datasource.getExternalUrl());
        }
        return datasourceProperties;
    }
}
