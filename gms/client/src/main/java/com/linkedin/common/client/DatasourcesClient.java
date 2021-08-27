package com.linkedin.common.client;

import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datasource.DatasourceKey;
import com.linkedin.restli.client.Client;

import javax.annotation.Nonnull;

public abstract class DatasourcesClient extends BaseClient {

    protected DatasourcesClient(@Nonnull Client restliClient) {
        super(restliClient);
    }

    @Nonnull
    protected DatasourceKey toDatasourceKey(@Nonnull DatasourceUrn urn) {
        return new DatasourceKey()
                .setName(urn.getDatasourceNameEntity())
                .setOrigin(urn.getOriginEntity())
                .setCategory(urn.getCategoryEntity());
    }
}
