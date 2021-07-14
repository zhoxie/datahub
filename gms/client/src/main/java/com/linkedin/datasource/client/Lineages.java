package com.linkedin.datasource.client;

import com.linkedin.common.client.DatasourcesClient;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datasource.DatasourceDownstreamLineage;
import com.linkedin.datasource.DownstreamLineageRequestBuilders;
import com.linkedin.datasource.DSUpstreamLineage;
import com.linkedin.datasource.DatasourceUpstreamLineageDelta;
import com.linkedin.datasource.UpstreamLineageRequestBuilders;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.ActionRequest;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;

import javax.annotation.Nonnull;

public class Lineages extends DatasourcesClient {

    private static final UpstreamLineageRequestBuilders UPSTREAM_LINEAGE_REQUEST_BUILDERS =
        new UpstreamLineageRequestBuilders();
    private static final DownstreamLineageRequestBuilders DOWNSTREAM_LINEAGE_REQUEST_BUILDERS =
        new DownstreamLineageRequestBuilders();

    public Lineages(@Nonnull Client restliClient) {
        super(restliClient);
    }

    /**
     * Gets a specific version of {@link DSUpstreamLineage} for the given datasource.
     */
    @Nonnull
    public DSUpstreamLineage getUpstreamLineage(@Nonnull DatasourceUrn datasourceUrn, long version)
            throws RemoteInvocationException {

        final GetRequest<DSUpstreamLineage> request = UPSTREAM_LINEAGE_REQUEST_BUILDERS.get()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .id(version)
                .build();
        return _client.sendRequest(request).getResponseEntity();
    }

    /**
     * Gets a specific version of {@link DatasourceDownstreamLineage} for the given datasource.
     */
    @Nonnull
    public DatasourceDownstreamLineage getDownstreamLineage(@Nonnull DatasourceUrn datasourceUrn)
        throws RemoteInvocationException {

        final GetRequest<DatasourceDownstreamLineage> request = DOWNSTREAM_LINEAGE_REQUEST_BUILDERS.get()
            .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
            .build();
        return _client.sendRequest(request).getResponseEntity();
    }

    /**
     * Similar to {@link #getUpstreamLineage(DatasourceUrn)} but returns the latest version.
     */
    @Nonnull
    public DSUpstreamLineage getUpstreamLineage(@Nonnull DatasourceUrn datasourceUrn) throws RemoteInvocationException {
        return getUpstreamLineage(datasourceUrn, BaseLocalDAO.LATEST_VERSION);
    }

    /**
     * Sets {@link DSUpstreamLineage} for a specific datasource.
     */
    public void setUpstreamLineage(@Nonnull DatasourceUrn datasourceUrn, @Nonnull DSUpstreamLineage upstreamLineage)
            throws RemoteInvocationException {

        final CreateIdRequest<Long, DSUpstreamLineage> request = UPSTREAM_LINEAGE_REQUEST_BUILDERS.create()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .input(upstreamLineage)
                .build();
        _client.sendRequest(request).getResponseEntity();
    }

    /**
     * Delta update for {@link DSUpstreamLineage}
     *
     * @param datasourceUrn datasourceUrn
     * @param delta upstreamDelta
     * @return updated {@link DSUpstreamLineage}
     * @throws RemoteInvocationException throws RemoteInvocationException
     */
    @Nonnull
    public DSUpstreamLineage deltaUpdateUpstreamLineage(@Nonnull DatasourceUrn datasourceUrn, @Nonnull DatasourceUpstreamLineageDelta delta)
            throws RemoteInvocationException {

        final ActionRequest<DSUpstreamLineage> request = UPSTREAM_LINEAGE_REQUEST_BUILDERS.actionDeltaUpdate()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .deltaParam(delta)
                .build();
        return _client.sendRequest(request).getResponseEntity();
    }
}
