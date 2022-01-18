package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.urn.CorpGroupUrn;
import com.linkedin.common.urn.DataPlatformUrn;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datasource.sources.HiveSource;
import com.linkedin.datasource.sources.PinotSource;
import com.linkedin.datasource.sources.PrestoSource;
import com.linkedin.datasource.sources.TrinoSource;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datasource.DatasourceConnectionGSB;
import com.linkedin.datasource.DatasourceConnectionPrimary;
import com.linkedin.datasource.DatasourceCustomDashboardInfo;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.datasource.sources.IcebergSource;
import com.linkedin.datasource.sources.KafkaMetadataSource;
import com.linkedin.datasource.sources.MysqlSource;
import com.linkedin.datasource.sources.OracleSource;
import com.linkedin.datasource.sources.PostgresSource;
import com.linkedin.datasource.sources.TiDBSource;
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


    public CreateDatasourceResolver(EntityClient datasourcesClient, EntityService entityService) {
        this.datasourcesClient = datasourcesClient;
        this.entityService = entityService;
    }

    private DataPlatformUrn parsePrimaryConn(Map<String, Object> primaryConnMap, DatasourceConnectionPrimary primaryConn) {
        DataPlatformUrn primaryPlatformUrn = null;
        if (primaryConnMap.containsKey(POSTGRES_SOURCE_NAME)) {
            PostgresSource postgres = ResolverUtils.bindArgument(primaryConnMap.get(POSTGRES_SOURCE_NAME), PostgresSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(postgres));
            primaryPlatformUrn = new DataPlatformUrn(POSTGRES_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(ORACLE_SOURCE_NAME)) {
            OracleSource oracle = ResolverUtils.bindArgument(primaryConnMap.get(ORACLE_SOURCE_NAME), OracleSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(oracle));
            primaryPlatformUrn = new DataPlatformUrn(ORACLE_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(ICEBERG_SOURCE_NAME)) {
            IcebergSource iceberg = ResolverUtils.bindArgument(primaryConnMap.get(ICEBERG_SOURCE_NAME), IcebergSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(iceberg));
            primaryPlatformUrn = new DataPlatformUrn(ICEBERG_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(KAFKA_SOURCE_NAME)) {
            KafkaMetadataSource kafka = ResolverUtils.bindArgument(primaryConnMap.get(KAFKA_SOURCE_NAME), KafkaMetadataSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(kafka));
            primaryPlatformUrn = new DataPlatformUrn(KAFKA_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(MYSQL_SOURCE_NAME)) {
            MysqlSource mysql = ResolverUtils.bindArgument(primaryConnMap.get(MYSQL_SOURCE_NAME), MysqlSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(mysql));
            primaryPlatformUrn = new DataPlatformUrn(MYSQL_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(HIVE_SOURCE_NAME)) {
            HiveSource hive = ResolverUtils.bindArgument(primaryConnMap.get(HIVE_SOURCE_NAME), HiveSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(hive));
            primaryPlatformUrn = new DataPlatformUrn(HIVE_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(PINOT_SOURCE_NAME)) {
            PinotSource piot = ResolverUtils.bindArgument(primaryConnMap.get(PINOT_SOURCE_NAME), PinotSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(piot));
            primaryPlatformUrn = new DataPlatformUrn(PINOT_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(PRESTO_SOURCE_NAME)) {
            PrestoSource presto = ResolverUtils.bindArgument(primaryConnMap.get(PRESTO_SOURCE_NAME), PrestoSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(presto));
            primaryPlatformUrn = new DataPlatformUrn(PRESTO_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(TIDB_SOURCE_NAME)) {
            TiDBSource tidb = ResolverUtils.bindArgument(primaryConnMap.get(TIDB_SOURCE_NAME), TiDBSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(tidb));
            primaryPlatformUrn = new DataPlatformUrn(TIDB_SOURCE_NAME);
        } else if (primaryConnMap.containsKey(TRINO_SOURCE_NAME)) {
            TrinoSource trino = ResolverUtils.bindArgument(primaryConnMap.get(TRINO_SOURCE_NAME), TrinoSource.class);
            primaryConn.setConnection(DatasourceConnectionPrimary.Connection.create(trino));
            primaryPlatformUrn = new DataPlatformUrn(TRINO_SOURCE_NAME);
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(primaryConnMap.keySet().toArray()));
        }
        return primaryPlatformUrn;
    }

    private DataPlatformUrn parseGSBConn(Map<String, Object> gsbConnMap, DatasourceConnectionGSB gsbConn) {
        DataPlatformUrn gsbPlatformUrn = null;
        if (gsbConnMap.containsKey(POSTGRES_SOURCE_NAME)) {
            PostgresSource postgres = ResolverUtils.bindArgument(gsbConnMap.get(POSTGRES_SOURCE_NAME), PostgresSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(postgres));
            gsbPlatformUrn = new DataPlatformUrn(POSTGRES_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(ORACLE_SOURCE_NAME)) {
            OracleSource oracle = ResolverUtils.bindArgument(gsbConnMap.get(ORACLE_SOURCE_NAME), OracleSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(oracle));
            gsbPlatformUrn = new DataPlatformUrn(ORACLE_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(ICEBERG_SOURCE_NAME)) {
            IcebergSource iceberg = ResolverUtils.bindArgument(gsbConnMap.get(ICEBERG_SOURCE_NAME), IcebergSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(iceberg));
            gsbPlatformUrn = new DataPlatformUrn(ICEBERG_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(KAFKA_SOURCE_NAME)) {
            KafkaMetadataSource kafka = ResolverUtils.bindArgument(gsbConnMap.get(KAFKA_SOURCE_NAME), KafkaMetadataSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(kafka));
            gsbPlatformUrn = new DataPlatformUrn(KAFKA_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(MYSQL_SOURCE_NAME)) {
            MysqlSource mysql = ResolverUtils.bindArgument(gsbConnMap.get(MYSQL_SOURCE_NAME), MysqlSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(mysql));
            gsbPlatformUrn = new DataPlatformUrn(MYSQL_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(HIVE_SOURCE_NAME)) {
            HiveSource hive = ResolverUtils.bindArgument(gsbConnMap.get(HIVE_SOURCE_NAME), HiveSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(hive));
            gsbPlatformUrn = new DataPlatformUrn(HIVE_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(PINOT_SOURCE_NAME)) {
            PinotSource piot = ResolverUtils.bindArgument(gsbConnMap.get(PINOT_SOURCE_NAME), PinotSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(piot));
            gsbPlatformUrn = new DataPlatformUrn(PINOT_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(PRESTO_SOURCE_NAME)) {
            PrestoSource presto = ResolverUtils.bindArgument(gsbConnMap.get(PRESTO_SOURCE_NAME), PrestoSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(presto));
            gsbPlatformUrn = new DataPlatformUrn(PRESTO_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(TIDB_SOURCE_NAME)) {
            TiDBSource tidb = ResolverUtils.bindArgument(gsbConnMap.get(TIDB_SOURCE_NAME), TiDBSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(tidb));
            gsbPlatformUrn = new DataPlatformUrn(TIDB_SOURCE_NAME);
        } else if (gsbConnMap.containsKey(TRINO_SOURCE_NAME)) {
            TrinoSource trino = ResolverUtils.bindArgument(gsbConnMap.get(TRINO_SOURCE_NAME), TrinoSource.class);
            gsbConn.setConnection(DatasourceConnectionGSB.Connection.create(trino));
            gsbPlatformUrn = new DataPlatformUrn(TRINO_SOURCE_NAME);
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(gsbConnMap.keySet().toArray()));
        }
        return gsbPlatformUrn;
    }

    private boolean isCustmDashboardSupportType(Map<String, Object> primaryConnMap) {
        return primaryConnMap.containsKey(ORACLE_SOURCE_NAME) || primaryConnMap.containsKey(TRINO_SOURCE_NAME)
                || primaryConnMap.containsKey(TIDB_SOURCE_NAME) || primaryConnMap.containsKey(PRESTO_SOURCE_NAME)
                || primaryConnMap.containsKey(HIVE_SOURCE_NAME) || primaryConnMap.containsKey(POSTGRES_SOURCE_NAME)
                || primaryConnMap.containsKey(MYSQL_SOURCE_NAME);
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

                datasourcesClient.ingestProposal(primaryConnProposal, context.getActor());
                if (hasGSB) {
                    datasourcesClient.ingestProposal(gsbConnProposal, context.getActor());
                }
                if (syncCDAPI) {
                    datasourcesClient.ingestProposal(customDashboardInfoProposal, context.getActor());
                }
                return datasourcesClient.ingestProposal(sourceInfoProposal, context.getActor());

            } catch (Exception e) {
                throw new RuntimeException("Failed to add datasource.", e);
            }
        });
    }
}
