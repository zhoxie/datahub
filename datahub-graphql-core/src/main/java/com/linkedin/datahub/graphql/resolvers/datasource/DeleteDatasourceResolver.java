package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datasource.DatasourceConnectionPrimary;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.entity.Entity;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.aspect.DatasourceAspect;
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

        Entity entity = datasourcesClient.get(urn, context.getActor());
        String groupUrn = null;
        String category = null;
        String region = null;
        String type = null;
        for (DatasourceAspect aspect : entity.getValue().getDatasourceSnapshot().getAspects()) {
            if (aspect.isDatasourceInfo()) {
                DatasourceInfo info = aspect.getDatasourceInfo();
                groupUrn = info.getGroup().toString();
                category = info.getCategory();
                region = info.getRegion();
            }
            if (aspect.isDatasourceConnectionPrimary()) {
                DatasourceConnectionPrimary.Connection conn = aspect.getDatasourceConnectionPrimary().getConnection();
                if (conn.isIcebergSource()) {
                    type = "iceberg";
                } else if (conn.isKafkaMetadataSource()) {
                    type = "kafka";
                } else if (conn.isMysqlSource()) {
                    type = "mysql";
                } else if (conn.isOracleSource()) {
                    type = "oracle";
                } else {
                    type = "postgres";
                }
            }
        }

        if ("true".equals(System.getenv("CUSTOM_DASHBOARD_API_ENABLE"))
                && System.getenv("CUSTOM_DASHBOARD_GROUP").equals(groupUrn)) {
            CustomDashboardAPIClient.deleteDatasource(urn.getEntityKey().get(1), category, type, region, CustomDashboardAPIUtil.getAccessToken());
        }

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
