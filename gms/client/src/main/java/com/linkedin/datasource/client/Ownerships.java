package com.linkedin.datasource.client;

import com.linkedin.common.Ownership;
import com.linkedin.common.client.DatasourcesClient;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.datasource.RawOwnershipRequestBuilders;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.linkedin.common.urn.UrnUtils.getAuditStamp;

public class Ownerships extends DatasourcesClient {

    private static final RawOwnershipRequestBuilders RAW_OWNERSHIP_REQUEST_BUILDERS = new RawOwnershipRequestBuilders();

    public Ownerships(@Nonnull Client restliClient) {
        super(restliClient);
    }

    /**
     * Gets datasource ownership by datasource Urn + version
     *
     * @param datasourceUrn DatasourceUrn
     * @param version long
     * @return Ownership
     * @throws RemoteInvocationException
     */
    @Nonnull
    public Ownership getOwnership(@Nonnull DatasourceUrn datasourceUrn, long version)
            throws RemoteInvocationException {
        GetRequest<Ownership> getRequest = RAW_OWNERSHIP_REQUEST_BUILDERS.get()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .id(version)
                .build();

        return _client.sendRequest(getRequest).getResponse().getEntity();
    }

    /**
     * Gets latest version of ownership info of a datasource.
     *
     * @param datasourceUrn DatasourceUrn
     * @return Ownership
     * @throws RemoteInvocationException
     */
    @Nonnull
    public Ownership getLatestOwnership(@Nonnull DatasourceUrn datasourceUrn) throws RemoteInvocationException {

        GetRequest<Ownership> req = RAW_OWNERSHIP_REQUEST_BUILDERS.get()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .id(BaseLocalDAO.LATEST_VERSION)
                .build();

        return _client.sendRequest(req).getResponse().getEntity();
    }

    /**
     * Create an ownership record for a datasource.
     *
     * @param datasourceUrn DatasourceUrn
     * @param ownership Ownership
     * @param actor Urn
     * @throws RemoteInvocationException
     */
    public void createOwnership(@Nonnull DatasourceUrn datasourceUrn, @Nonnull Ownership ownership, @Nullable Urn actor)
            throws RemoteInvocationException {

        ownership.setLastModified(getAuditStamp(actor).setTime(System.currentTimeMillis()));

        CreateIdRequest<Long, Ownership> req = RAW_OWNERSHIP_REQUEST_BUILDERS.create()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .input(ownership)
                .build();

        _client.sendRequest(req).getResponseEntity();
    }
}
