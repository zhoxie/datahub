package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.urn.CorpGroupUrn;
import com.linkedin.common.FabricType;
import com.linkedin.common.urn.DataPlatformUrn;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datasource.DatasourceConnectionGSB;
import com.linkedin.datasource.DatasourceConnectionPrimary;
import com.linkedin.datasource.DatasourceInfo;
import com.linkedin.datasource.sources.IcebergSource;
import com.linkedin.datasource.sources.KafkaMetadataSource;
import com.linkedin.datasource.sources.MysqlSource;
import com.linkedin.datasource.sources.OracleSource;
import com.linkedin.datasource.sources.PostgresSource;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.events.metadata.ChangeType;
import com.linkedin.metadata.utils.GenericAspectUtils;
import com.linkedin.mxe.MetadataChangeProposal;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CreateDatasourceResolver implements DataFetcher<CompletableFuture<String>> {
    private final EntityClient datasourcesClient;

    private static final String KAFKA_SOURCE_NAME = "kafka";
    private static final String ORACLE_SOURCE_NAME = "oracle";
    private static final String MYSQL_SOURCE_NAME = "mysql";
    private static final String ICEBERG_SOURCE_NAME = "iceberg";
    private static final String POSTGRES_SOURCE_NAME = "postgres";


    public CreateDatasourceResolver(EntityClient datasourcesClient) {
        this.datasourcesClient = datasourcesClient;
    }

    @Override
    public CompletableFuture<String> get(DataFetchingEnvironment environment) throws Exception {
        final QueryContext context = environment.getContext();

        DataPlatformUrn primaryPlatformUrn = null;

        Map<String, Object> inputMap = environment.getArgument("input");

        String sourceName = (String) inputMap.get("name");

        final DatasourceInfo datasourceInfo = new DatasourceInfo();
        if (inputMap.containsKey("category")) {
            String sourceCategory = (String) inputMap.get("category");
            datasourceInfo.setCategory(sourceCategory);
        }
        if (inputMap.containsKey("region")) {
            String sourceRegion = (String) inputMap.get("region");
            datasourceInfo.setRegion(sourceRegion);
        }
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
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(primaryConnMap.keySet().toArray()));
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
            } else {
                throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(gsbConnMap.keySet().toArray()));
            }
        }

        if (gsbPlatformUrn != null && !primaryPlatformUrn.equals(gsbPlatformUrn)) {
            throw new IllegalArgumentException("GSB platform was different from primary platform.");
        }

        DatasourceUrn sourceUrn = new DatasourceUrn(primaryPlatformUrn, sourceName, FabricType.PROD);

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
            gsbConnProposal.setAspect(GenericAspectUtils.serializeAspect(primaryConn));
            gsbConnProposal.setChangeType(ChangeType.UPSERT);
        }

        final boolean hasGSB = gsbConn != null;

        return CompletableFuture.supplyAsync(() -> {
            try {

                datasourcesClient.ingestProposal(primaryConnProposal, context.getActor());
                if (hasGSB) {
                    datasourcesClient.ingestProposal(primaryConnProposal, context.getActor());
                }
                return datasourcesClient.ingestProposal(sourceInfoProposal, context.getActor());

            } catch (Exception e) {
                throw new RuntimeException("Failed to add datasource", e);
            }
        });
    }
}
