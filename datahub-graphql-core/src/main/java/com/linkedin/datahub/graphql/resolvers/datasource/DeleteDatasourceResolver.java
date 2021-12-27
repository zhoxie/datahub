package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.entity.client.EntityClient;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.concurrent.CompletableFuture;

public class DeleteDatasourceResolver implements DataFetcher<CompletableFuture<String>> {
    private final EntityClient datasourcesClient;

    public DeleteDatasourceResolver(EntityClient datasourcesClient) {
        this.datasourcesClient = datasourcesClient;
    }

    @Override
    public CompletableFuture<String> get(DataFetchingEnvironment environment) throws Exception {
        final QueryContext context = environment.getContext();
        final String datasourceUrn = environment.getArgument("urn");
        final Urn urn = Urn.createFromString(datasourceUrn);
        return CompletableFuture.supplyAsync(() -> {
            try {
                datasourcesClient.deleteEntity(urn, context.getActor());
                return datasourceUrn;
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to perform delete against datasourrce with urn %s", datasourceUrn), e);
            }
        });
    }
}
