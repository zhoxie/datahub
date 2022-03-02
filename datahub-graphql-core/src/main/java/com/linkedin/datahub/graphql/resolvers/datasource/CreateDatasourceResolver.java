package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.urn.CorpGroupUrn;
import com.linkedin.common.urn.DataPlatformUrn;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datasource.DatasourceConnectionGSB;
import com.linkedin.datasource.DatasourceConnectionPrimary;
import com.linkedin.datasource.DatasourceCustomDashboardInfo;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.datasource.sources.PostgresSource;
import com.linkedin.datasource.sources.OracleSource;
import com.linkedin.datasource.sources.IcebergSource;
import com.linkedin.datasource.sources.KafkaMetadataSource;
import com.linkedin.datasource.sources.MysqlSource;
import com.linkedin.datasource.sources.HiveSource;
import com.linkedin.datasource.sources.PinotSource;
import com.linkedin.datasource.sources.PrestoSource;
import com.linkedin.datasource.sources.SnowflakeSource;
import com.linkedin.datasource.sources.TiDBSource;
import com.linkedin.datasource.sources.TrinoSource;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.events.metadata.ChangeType;
import com.linkedin.metadata.entity.EntityService;
import com.linkedin.metadata.utils.GenericAspectUtils;
import com.linkedin.mxe.MetadataChangeProposal;
import com.linkedin.util.Configuration;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CreateDatasourceResolver implements DataFetcher<CompletableFuture<String>> {
    private final EntityClient datasourcesClient;
    private final EntityService entityService;


    public CreateDatasourceResolver(EntityClient datasourcesClient, EntityService entityService) {
        this.datasourcesClient = datasourcesClient;
        this.entityService = entityService;
    }

    private DataPlatformUrn parsePrimaryConn(Map<String, Object> primaryConnMap, DatasourceConnectionPrimary primaryConn) {
        DataPlatformUrn primaryPlatformUrn = null;
        if (primaryConnMap.containsKey(DatasourceConstants.POSTGRES_SOURCE_NAME)) {
            PostgresSource postgres = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.POSTGRES_SOURCE_NAME), PostgresSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(postgres));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.POSTGRES_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.ORACLE_SOURCE_NAME)) {
            OracleSource oracle = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.ORACLE_SOURCE_NAME), OracleSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(oracle));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.ORACLE_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.ICEBERG_SOURCE_NAME)) {
            IcebergSource iceberg = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.ICEBERG_SOURCE_NAME), IcebergSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(iceberg));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.ICEBERG_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.KAFKA_SOURCE_NAME)) {
            KafkaMetadataSource kafka = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.KAFKA_SOURCE_NAME), KafkaMetadataSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(kafka));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.KAFKA_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.MYSQL_SOURCE_NAME)) {
            MysqlSource mysql = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.MYSQL_SOURCE_NAME), MysqlSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(mysql));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.MYSQL_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.HIVE_SOURCE_NAME)) {
            HiveSource hive = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.HIVE_SOURCE_NAME), HiveSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(hive));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.HIVE_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.PINOT_SOURCE_NAME)) {
            PinotSource piot = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.PINOT_SOURCE_NAME), PinotSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(piot));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.PINOT_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.PRESTO_SOURCE_NAME)) {
            PrestoSource presto = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.PRESTO_SOURCE_NAME), PrestoSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(presto));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.PRESTO_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.TIDB_SOURCE_NAME)) {
            TiDBSource tidb = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.TIDB_SOURCE_NAME), TiDBSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(tidb));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.TIDB_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.TRINO_SOURCE_NAME)) {
            TrinoSource trino = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.TRINO_SOURCE_NAME), TrinoSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(trino));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.TRINO_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(DatasourceConstants.SNOWFLAKE_SOURCE_NAME)) {
            SnowflakeSource snowflake = ResolverUtils.bindArgument(primaryConnMap.get(DatasourceConstants.SNOWFLAKE_SOURCE_NAME), SnowflakeSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(snowflake));
            primaryPlatformUrn = new DataPlatformUrn(DatasourceConstants.SNOWFLAKE_SOURCE_NAME);
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(primaryConnMap.keySet().toArray()));
        }
        return primaryPlatformUrn;
    }

    private DataPlatformUrn parseGSBConn(Map<String, Object> gsbConnMap, DatasourceConnectionGSB gsbConn) {
        DataPlatformUrn gsbPlatformUrn = null;
        if (gsbConnMap.containsKey(DatasourceConstants.POSTGRES_SOURCE_NAME)) {
            PostgresSource postgres = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.POSTGRES_SOURCE_NAME), PostgresSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(postgres));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.POSTGRES_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.ORACLE_SOURCE_NAME)) {
            OracleSource oracle = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.ORACLE_SOURCE_NAME), OracleSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(oracle));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.ORACLE_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.ICEBERG_SOURCE_NAME)) {
            IcebergSource iceberg = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.ICEBERG_SOURCE_NAME), IcebergSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(iceberg));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.ICEBERG_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.KAFKA_SOURCE_NAME)) {
            KafkaMetadataSource kafka = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.KAFKA_SOURCE_NAME), KafkaMetadataSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(kafka));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.KAFKA_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.MYSQL_SOURCE_NAME)) {
            MysqlSource mysql = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.MYSQL_SOURCE_NAME), MysqlSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(mysql));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.MYSQL_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.HIVE_SOURCE_NAME)) {
            HiveSource hive = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.HIVE_SOURCE_NAME), HiveSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(hive));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.HIVE_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.PINOT_SOURCE_NAME)) {
            PinotSource piot = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.PINOT_SOURCE_NAME), PinotSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(piot));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.PINOT_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.PRESTO_SOURCE_NAME)) {
            PrestoSource presto = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.PRESTO_SOURCE_NAME), PrestoSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(presto));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.PRESTO_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.TIDB_SOURCE_NAME)) {
            TiDBSource tidb = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.TIDB_SOURCE_NAME), TiDBSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(tidb));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.TIDB_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.TRINO_SOURCE_NAME)) {
            TrinoSource trino = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.TRINO_SOURCE_NAME), TrinoSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(trino));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.TRINO_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(DatasourceConstants.SNOWFLAKE_SOURCE_NAME)) {
            SnowflakeSource snowflake = ResolverUtils.bindArgument(gsbConnMap.get(DatasourceConstants.SNOWFLAKE_SOURCE_NAME), SnowflakeSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(snowflake));
            gsbPlatformUrn = new DataPlatformUrn(DatasourceConstants.SNOWFLAKE_SOURCE_NAME);
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(gsbConnMap.keySet().toArray()));
        }
        return gsbPlatformUrn;
    }

    private boolean isCustmDashboardSupportType(Map<String, Object> primaryConnMap) {
        return primaryConnMap.containsKey(DatasourceConstants.ORACLE_SOURCE_NAME) || primaryConnMap.containsKey(DatasourceConstants.TRINO_SOURCE_NAME)
                || primaryConnMap.containsKey(DatasourceConstants.TIDB_SOURCE_NAME) || primaryConnMap.containsKey(DatasourceConstants.PRESTO_SOURCE_NAME)
                || primaryConnMap.containsKey(DatasourceConstants.HIVE_SOURCE_NAME) || primaryConnMap.containsKey(DatasourceConstants.POSTGRES_SOURCE_NAME)
                || primaryConnMap.containsKey(DatasourceConstants.MYSQL_SOURCE_NAME);
    }

    @Override
    public CompletableFuture<String> get(DataFetchingEnvironment environment) throws Exception {
        final QueryContext context = environment.getContext();

        Map<String, Object> inputMap = environment.getArgument("input");
        String sourceName = (String) inputMap.get("name");

        final DatasourceInfo datasourceInfo = new DatasourceInfo();

        datasourceInfo.setCategory(Configuration.getEnvironmentVariable("CUSTOM_DASHBOARD_API_CATEGORY"));
        String sourceRegion = (String) inputMap.get("region");
        datasourceInfo.setRegion(sourceRegion);

        if (inputMap.containsKey("group")) {
            String groupRawUrn = (String) inputMap.get("group");
            CorpGroupUrn corpGroupUrn = CorpGroupUrn.createFromString(groupRawUrn);
            datasourceInfo.setGroup(corpGroupUrn);
        }

        final DatasourceConnectionPrimary primaryConn = new DatasourceConnectionPrimary();

        Map<String, Object> primaryConnMap = (Map<String, Object>) inputMap.get("primaryConn");
        if (primaryConnMap.containsKey("dataCenter")) {
            String dataCenter = (String) primaryConnMap.get("dataCenter");
            primaryConn.setDataCenter(dataCenter);
        }

        DataPlatformUrn primaryPlatformUrn = parsePrimaryConn(primaryConnMap, primaryConn);

        DatasourceUrn sourceUrn = new DatasourceUrn(primaryPlatformUrn, sourceName, sourceRegion);
        boolean create = (boolean) inputMap.get("create");
        if (create && entityService.exists(sourceUrn)) {
            throw new IllegalArgumentException("Failed to add data source, duplicate data source!");
        }

        DatasourceConnectionGSB gsbConn = null;
        DataPlatformUrn gsbPlatformUrn = null;
        if (inputMap.containsKey("gsbConn")) {
            gsbConn = new DatasourceConnectionGSB();
            Map<String, Object> gsbConnMap = (Map<String, Object>) inputMap.get("gsbConn");
            if (gsbConnMap.containsKey("dataCenter")) {
                String dataCenter = (String) gsbConnMap.get("dataCenter");
                gsbConn.setDataCenter(dataCenter);
            }

            gsbPlatformUrn = parseGSBConn(gsbConnMap, gsbConn);
        }

        if (gsbPlatformUrn != null && !primaryPlatformUrn.equals(gsbPlatformUrn)) {
            throw new IllegalArgumentException("GSB platform was different from primary platform.");
        }

        final MetadataChangeProposal sourceInfoProposal = new MetadataChangeProposal();
        sourceInfoProposal.setEntityUrn(sourceUrn);
        sourceInfoProposal.setAspectName("datasourceInfo");
        sourceInfoProposal.setEntityType("datasource");
        sourceInfoProposal.setAspect(GenericAspectUtils.serializeAspect(datasourceInfo));
        sourceInfoProposal.setChangeType(ChangeType.UPSERT);

        final MetadataChangeProposal primaryConnProposal = new MetadataChangeProposal();
        primaryConnProposal.setEntityUrn(sourceUrn);
        primaryConnProposal.setAspectName("datasourceConnectionPrimary");
        primaryConnProposal.setEntityType("datasource");
        primaryConnProposal.setAspect(GenericAspectUtils.serializeAspect(primaryConn));
        primaryConnProposal.setChangeType(ChangeType.UPSERT);

        final MetadataChangeProposal gsbConnProposal = new MetadataChangeProposal();
        if (gsbConn != null) {
            gsbConnProposal.setEntityUrn(sourceUrn);
            gsbConnProposal.setAspectName("datasourceConnectionGSB");
            gsbConnProposal.setEntityType("datasource");
            gsbConnProposal.setAspect(GenericAspectUtils.serializeAspect(gsbConn));
            gsbConnProposal.setChangeType(ChangeType.UPSERT);
        }

        final boolean hasGSB = gsbConn != null;

        String customDashboardRequestBody = null;
        String customDashboardResponse = null;
        final MetadataChangeProposal customDashboardInfoProposal = new MetadataChangeProposal();
        final boolean syncCDAPI = (boolean) inputMap.get("syncCDAPI") && isCustmDashboardSupportType(primaryConnMap);
        if ("true".equals(Configuration.getEnvironmentVariable("CUSTOM_DASHBOARD_API_ENABLE")) && syncCDAPI) {
            customDashboardRequestBody = CustomDashboardAPIUtil.buildCreateRequestBody(inputMap);
            customDashboardResponse = CustomDashboardAPIClient.createDatasource(customDashboardRequestBody, CustomDashboardAPIUtil.getAccessToken());

            customDashboardInfoProposal.setEntityUrn(sourceUrn);
            customDashboardInfoProposal.setAspectName("datasourceCustomDashboardInfo");
            customDashboardInfoProposal.setEntityType("datasource");
            final DatasourceCustomDashboardInfo customDashboardInfo = new DatasourceCustomDashboardInfo();
            customDashboardInfo.setPostParam(customDashboardRequestBody);
            customDashboardInfo.setResponse(customDashboardResponse);
            customDashboardInfoProposal.setAspect(GenericAspectUtils.serializeAspect(customDashboardInfo));
            customDashboardInfoProposal.setChangeType(ChangeType.UPSERT);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {

                datasourcesClient.ingestProposal(primaryConnProposal, context.getAuthentication());
                if (hasGSB) {
                    datasourcesClient.ingestProposal(gsbConnProposal, context.getAuthentication());
                }
                if (syncCDAPI) {
                    datasourcesClient.ingestProposal(customDashboardInfoProposal, context.getAuthentication());
                }
                return datasourcesClient.ingestProposal(sourceInfoProposal, context.getAuthentication());

            } catch (Exception e) {
                throw new RuntimeException("Failed to add datasource.", e);
            }
        });
    }
}
