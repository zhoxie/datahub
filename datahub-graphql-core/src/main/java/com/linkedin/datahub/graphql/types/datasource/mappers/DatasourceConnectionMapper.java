package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.datahub.graphql.generated.DatasourceConnection;
import com.linkedin.datahub.graphql.generated.IcebergSource;
import com.linkedin.datahub.graphql.generated.KafkaMetadataSource;
import com.linkedin.datahub.graphql.generated.MysqlSource;
import com.linkedin.datahub.graphql.generated.OracleSource;
import com.linkedin.datahub.graphql.generated.PostgresSource;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;

import javax.annotation.Nonnull;

public class DatasourceConnectionMapper implements ModelMapper<com.linkedin.datasource.DatasourceConnection, DatasourceConnection> {

    public static final DatasourceConnectionMapper INSTANCE = new DatasourceConnectionMapper();

    public static DatasourceConnection map(@Nonnull final com.linkedin.datasource.DatasourceConnection connections) {
        return INSTANCE.apply(connections);
    }

    @Override
    public DatasourceConnection apply(@Nonnull final com.linkedin.datasource.DatasourceConnection input) {
        final DatasourceConnection result = new DatasourceConnection();
        result.setCategory(input.getCategory());
        if (input.getConnection().isIcebergSource()) {
            IcebergSource icebergSource = new IcebergSource();
            icebergSource.setHiveMetastoreUris(input.getConnection().getIcebergSource().getHiveMetastoreUris());
            result.setConnection(icebergSource);
        } else if (input.getConnection().isKafkaMetadataSource()) {
            KafkaMetadataSource conn = new KafkaMetadataSource();
            com.linkedin.datasource.sources.KafkaMetadataSource source = input.getConnection().getKafkaMetadataSource();
            conn.setBootstrap(source.getBootstrap());
            conn.setSchemaRegistryUrl(source.getSchemaRegistryUrl());
            conn.setTopicPatternsAllow(source.getTopicPatternsAllow());
            conn.setTopicPatternsDeny(source.getTopicPatternsDeny());
            conn.setTopicPatternsIgnoreCase(source.isTopicPatternsIgnoreCase());
            result.setConnection(conn);
        } else if (input.getConnection().isMysqlSource()) {
            MysqlSource conn = new MysqlSource();
            com.linkedin.datasource.sources.MysqlSource source = input.getConnection().getMysqlSource();
            conn.setUsername(source.getUsername());
            conn.setPassword(source.getPassword());
            conn.setHostPort(source.getHostPort());
            conn.setDatabase(source.getDatabase());
            conn.setDatabaseAlias(source.getDatabaseAlias());
            conn.setIncludeTables(source.isIncludeTables());
            conn.setIncludeViews(source.isIncludeViews());
            conn.setTablePatternAllow(source.getTablePatternAllow());
            conn.setTablePatternDeny(source.getTablePatternDeny());
            conn.setTablePatternIgnoreCase(source.isTablePatternIgnoreCase());
            conn.setSchemaPatternAllow(source.getSchemaPatternAllow());
            conn.setSchemaPatternDeny(source.getSchemaPatternDeny());
            conn.setSchemaPatternIgnoreCase(source.isSchemaPatternIgnoreCase());
            conn.setViewPatternAllow(source.getViewPatternAllow());
            conn.setViewPatternDeny(source.getViewPatternDeny());
            conn.setViewPatternIgnoreCase(source.isViewPatternIgnoreCase());
            result.setConnection(conn);
        } else if (input.getConnection().isPostgresSource()) {
            PostgresSource conn = new PostgresSource();
            com.linkedin.datasource.sources.PostgresSource source = input.getConnection().getPostgresSource();
            conn.setUsername(source.getUsername());
            conn.setPassword(source.getPassword());
            conn.setHostPort(source.getHostPort());
            conn.setDatabase(source.getDatabase());
            conn.setDatabaseAlias(source.getDatabaseAlias());
            conn.setIncludeTables(source.isIncludeTables());
            conn.setIncludeViews(source.isIncludeViews());
            conn.setTablePatternAllow(source.getTablePatternAllow());
            conn.setTablePatternDeny(source.getTablePatternDeny());
            conn.setTablePatternIgnoreCase(source.isTablePatternIgnoreCase());
            conn.setSchemaPatternAllow(source.getSchemaPatternAllow());
            conn.setSchemaPatternDeny(source.getSchemaPatternDeny());
            conn.setSchemaPatternIgnoreCase(source.isSchemaPatternIgnoreCase());
            conn.setViewPatternAllow(source.getViewPatternAllow());
            conn.setViewPatternDeny(source.getViewPatternDeny());
            conn.setViewPatternIgnoreCase(source.isViewPatternIgnoreCase());
            result.setConnection(conn);
        } else if (input.getConnection().isOracleSource()) {
            OracleSource conn = new OracleSource();
            com.linkedin.datasource.sources.OracleSource source = input.getConnection().getOracleSource();
            conn.setUsername(source.getUsername());
            conn.setPassword(source.getPassword());
            conn.setHostPort(source.getHostPort());
            conn.setDatabase(source.getDatabase());
            conn.setServiceName(source.getServiceName());
            conn.setDatabaseAlias(source.getDatabaseAlias());
            conn.setIncludeTables(source.isIncludeTables());
            conn.setIncludeViews(source.isIncludeViews());
            conn.setTablePatternAllow(source.getTablePatternAllow());
            conn.setTablePatternDeny(source.getTablePatternDeny());
            conn.setTablePatternIgnoreCase(source.isTablePatternIgnoreCase());
            conn.setSchemaPatternAllow(source.getSchemaPatternAllow());
            conn.setSchemaPatternDeny(source.getSchemaPatternDeny());
            conn.setSchemaPatternIgnoreCase(source.isSchemaPatternIgnoreCase());
            conn.setViewPatternAllow(source.getViewPatternAllow());
            conn.setViewPatternDeny(source.getViewPatternDeny());
            conn.setViewPatternIgnoreCase(source.isViewPatternIgnoreCase());
            result.setConnection(conn);
        }
        return result;
    }
}
