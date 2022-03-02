package com.linkedin.datahub.graphql.resolvers.datasource;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TestDatasourceResolver implements DataFetcher<CompletableFuture<Boolean>> {

    public TestDatasourceResolver() {
    }

    @Override
    public CompletableFuture<Boolean> get(DataFetchingEnvironment environment) throws Exception {
        Map<String, Object> inputMap = environment.getArgument("input");
        Map<String, Object> connMap = (Map<String, Object>) inputMap.get("connection");
        String type;
        Map<String, Object> dbMap;

        if (connMap.containsKey(DatasourceConstants.POSTGRES_SOURCE_NAME)) {
            type = DatasourceConstants.POSTGRES_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.POSTGRES_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.ORACLE_SOURCE_NAME)) {
            type = DatasourceConstants.ORACLE_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.ORACLE_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.ICEBERG_SOURCE_NAME)) {
            type = DatasourceConstants.ICEBERG_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.ICEBERG_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.KAFKA_SOURCE_NAME)) {
            type = DatasourceConstants.KAFKA_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.KAFKA_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.MYSQL_SOURCE_NAME)) {
            type = DatasourceConstants.MYSQL_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.MYSQL_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.HIVE_SOURCE_NAME)) {
            type = DatasourceConstants.HIVE_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.HIVE_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.PINOT_SOURCE_NAME)) {
            type = DatasourceConstants.PINOT_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.PINOT_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.PRESTO_SOURCE_NAME)) {
            type = DatasourceConstants.PRESTO_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.PRESTO_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.TIDB_SOURCE_NAME)) {
            type = DatasourceConstants.TIDB_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.TIDB_SOURCE_NAME);
        } else if (connMap.containsKey(DatasourceConstants.TRINO_SOURCE_NAME)) {
            type = DatasourceConstants.TRINO_SOURCE_NAME;
            dbMap = (Map<String, Object>) connMap.get(DatasourceConstants.TRINO_SOURCE_NAME);
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(connMap.keySet().toArray()));
        }

        if (CustomDashboardAPIUtil.supportType(type) || DatasourceConstants.MYSQL_SOURCE_NAME.equals(type)) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return CustomDashboardAPIClient.testConnection(CustomDashboardAPIUtil.buildTestRequestBody(type, dbMap),
                            CustomDashboardAPIUtil.getAccessToken());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to add datasource.", e);
                }
            });
        }

        throw new IllegalArgumentException("Not support type:" + type);
    }
}
