package com.linkedin.datasource.client;

import com.linkedin.common.client.DatasourcesClient;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datasource.InstitutionalMemoryRequestBuilders;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;

import javax.annotation.Nonnull;

public class InstitutionalMemory extends DatasourcesClient {

    private static final InstitutionalMemoryRequestBuilders INSTITUTIONAL_MEMORY_REQUEST_BUILDERS
            = new InstitutionalMemoryRequestBuilders();

    public InstitutionalMemory(@Nonnull Client restliClient) {
        super(restliClient);
    }

    /**
     * Creates or Updates InstitutionalMemory aspect
     *
     * @param datasourceUrn datasource urn
     * @param institutionalMemory institutional memory
     */
    public void updateInstitutionalMemory(@Nonnull DatasourceUrn datasourceUrn,
                                          @Nonnull com.linkedin.common.InstitutionalMemory institutionalMemory)
            throws RemoteInvocationException {

        CreateIdRequest<Long, com.linkedin.common.InstitutionalMemory> request = INSTITUTIONAL_MEMORY_REQUEST_BUILDERS.create()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .input(institutionalMemory)
                .build();

        _client.sendRequest(request).getResponse();
    }

    /**
     * Get InstitutionalMemory aspect
     *
     * @param datasourceUrn datasource urn
     */
    @Nonnull
    public com.linkedin.common.InstitutionalMemory getInstitutionalMemory(@Nonnull DatasourceUrn datasourceUrn)
            throws RemoteInvocationException {

        Request<com.linkedin.common.InstitutionalMemory> request =
                INSTITUTIONAL_MEMORY_REQUEST_BUILDERS.get()
                        .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                        .id(BaseLocalDAO.LATEST_VERSION)
                        .build();

        return _client.sendRequest(request).getResponse().getEntity();
    }
}
