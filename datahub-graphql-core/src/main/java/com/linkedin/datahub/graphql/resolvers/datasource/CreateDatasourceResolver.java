package com.linkedin.datahub.graphql.resolvers.datasource;

import com.linkedin.common.FabricType;
import com.linkedin.common.urn.DataPlatformUrn;
import com.linkedin.common.urn.DatasourceUrn;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datasource.DatasourceConnection;
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

    private final String KAFKA_SOURCE_NAME = "kafka";
    private final String ORACLE_SOURCE_NAME = "oracle";
    private final String MYSQL_SOURCE_NAME = "mysql";
    private final String ICEBERG_SOURCE_NAME = "iceberg";
    private final String POSTGRES_SOURCE_NAME = "postgres";


    public CreateDatasourceResolver(EntityClient datasourcesClient) {
        this.datasourcesClient = datasourcesClient;
    }

    @Override
    public CompletableFuture<String> get(DataFetchingEnvironment environment) throws Exception {
        final QueryContext context = environment.getContext();

        DataPlatformUrn platformUrn;

        Map<String, Object> inputMap = environment.getArgument("input");

        String sourceName = (String)inputMap.get("name");

        final DatasourceConnection conn = new DatasourceConnection();
        if(inputMap.containsKey("category")) {
            String sourceCategory = (String)inputMap.get("category");
            conn.setCategory(sourceCategory);
        }
        Map<String, Object> connMap = (Map<String, Object>)inputMap.get("connection");
        if(connMap.containsKey(POSTGRES_SOURCE_NAME)) {
            PostgresSource postgres = ResolverUtils.bindArgument(connMap.get(POSTGRES_SOURCE_NAME), PostgresSource.class);
            conn.setConnection(DatasourceConnection.Connection.create(postgres));
            platformUrn = new DataPlatformUrn(POSTGRES_SOURCE_NAME);
        } else if(connMap.containsKey(ORACLE_SOURCE_NAME)) {
            OracleSource oracle = ResolverUtils.bindArgument(connMap.get(ORACLE_SOURCE_NAME), OracleSource.class);
            conn.setConnection(DatasourceConnection.Connection.create(oracle));
            platformUrn = new DataPlatformUrn(ORACLE_SOURCE_NAME);
        } else if(connMap.containsKey(ICEBERG_SOURCE_NAME)) {
            IcebergSource iceberg = ResolverUtils.bindArgument(connMap.get(ICEBERG_SOURCE_NAME), IcebergSource.class);
            conn.setConnection(DatasourceConnection.Connection.create(iceberg));
            platformUrn = new DataPlatformUrn(ICEBERG_SOURCE_NAME);
        } else if(connMap.containsKey(KAFKA_SOURCE_NAME)) {
            KafkaMetadataSource kafka = ResolverUtils.bindArgument(connMap.get(KAFKA_SOURCE_NAME), KafkaMetadataSource.class);
            conn.setConnection(DatasourceConnection.Connection.create(kafka));
            platformUrn = new DataPlatformUrn(KAFKA_SOURCE_NAME);
        } else if(connMap.containsKey(MYSQL_SOURCE_NAME)) {
            MysqlSource mysql = ResolverUtils.bindArgument(connMap.get(MYSQL_SOURCE_NAME), MysqlSource.class);
            conn.setConnection(DatasourceConnection.Connection.create(mysql));
            platformUrn = new DataPlatformUrn(MYSQL_SOURCE_NAME);
        } else {
            throw new IllegalArgumentException("Unknown source type: " + Arrays.toString(connMap.keySet().toArray()));
        }

        DatasourceUrn sourceUrn = new DatasourceUrn(platformUrn, sourceName, FabricType.PROD);

        final MetadataChangeProposal proposal = new MetadataChangeProposal();
        proposal.setEntityUrn(sourceUrn);
        proposal.setAspectName("datasourceConnection");
        proposal.setEntityType("datasource");
        proposal.setAspect(GenericAspectUtils.serializeAspect(conn));
        proposal.setChangeType(ChangeType.UPSERT);
        return CompletableFuture.supplyAsync(() ->{
            try {
                return datasourcesClient.ingestProposal(proposal, context.getActor());
            } catch (Exception e){
                throw new RuntimeException("Failed to add datasource", e);
            }
        });
    }
}
