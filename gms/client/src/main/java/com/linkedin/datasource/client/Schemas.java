package com.linkedin.datasource.client;

import com.linkedin.common.client.DatasourcesClient;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datasource.SchemaRequestBuilders;
import com.linkedin.metadata.dao.BaseLocalDAO;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.schema.SchemaMetadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Schemas extends DatasourcesClient {

    private static final SchemaRequestBuilders SCHEMA_REQUEST_BUILDERS = new SchemaRequestBuilders();

    public Schemas(@Nonnull Client restliClient) {
        super(restliClient);
    }

    /**
     * Get latest version of schema associated with a datasource Urn
     * @param datasourceUrn DatasourceUrn
     * @return SchemaMetadata
     * @throws RemoteInvocationException
     */
    @Nullable
    public SchemaMetadata getLatestSchemaByDatasource(@Nonnull DatasourceUrn datasourceUrn) throws RemoteInvocationException {
        GetRequest<SchemaMetadata> req = SCHEMA_REQUEST_BUILDERS.get()
                .datasourceKey(new ComplexResourceKey<>(toDatasourceKey(datasourceUrn), new EmptyRecord()))
                .id(BaseLocalDAO.LATEST_VERSION)
                .build();

        return _client.sendRequest(req).getResponse().getEntity();
    }
}
