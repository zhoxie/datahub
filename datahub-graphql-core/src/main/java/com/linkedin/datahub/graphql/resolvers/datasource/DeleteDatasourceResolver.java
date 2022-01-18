package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.urn.Urn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datasource.DatasourceConnectionPrimary;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.entity.Entity;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.aspect.DatasourceAspect;
import com.linkedin.util.Configuration;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.concurrent.CompletableFuture;

public class DeleteDatasourceResolver implements DataFetcher<CompletableFuture<String>> {
    private final EntityClient datasourcesClient;
    static final String KAFKA_SOURCE_NAME = "kafka";
    static final String ORACLE_SOURCE_NAME = "oracle";
    static final String MYSQL_SOURCE_NAME = "mysql";
    static final String ICEBERG_SOURCE_NAME = "iceberg";
    static final String POSTGRES_SOURCE_NAME = "postgres";
    static final String HIVE_SOURCE_NAME = "hive";
    static final String PINOT_SOURCE_NAME = "pinot";
    static final String PRESTO_SOURCE_NAME = "presto";
    static final String TIDB_SOURCE_NAME = "tiDB";
    static final String TRINO_SOURCE_NAME = "trino";

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
        boolean sync = false;
        boolean supportType = false;
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
                    type = ICEBERG_SOURCE_NAME;
                } else if (conn.isKafkaMetadataSource()) {
                    type = KAFKA_SOURCE_NAME;
                } else if (conn.isMysqlSource()) {
                    type = MYSQL_SOURCE_NAME;
                } else if (conn.isOracleSource()) {
                    type = ORACLE_SOURCE_NAME;
                    supportType = true;
                } else if (conn.isHiveSource()) {
                    type = HIVE_SOURCE_NAME;
                    supportType = true;
                } else if (conn.isPinotSource()) {
                    type = PINOT_SOURCE_NAME;
                    supportType = true;
                } else if (conn.isPrestoSource()) {
                    type = PRESTO_SOURCE_NAME;
                    supportType = true;
                } else if (conn.isTiDBSource()) {
                    type = TIDB_SOURCE_NAME;
                    supportType = true;
                } else if (conn.isTrinoSource()) {
                    type = TRINO_SOURCE_NAME;
                    supportType = true;
                } else {
                    type = POSTGRES_SOURCE_NAME;
                    supportType = true;
                }
            }
            if (aspect.isDatasourceCustomDashboardInfo()) {
                sync = true;
            }
        }

        if ("true".equals(Configuration.getEnvironmentVariable("CUSTOM_DASHBOARD_API_ENABLE")) && sync && supportType) {
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
