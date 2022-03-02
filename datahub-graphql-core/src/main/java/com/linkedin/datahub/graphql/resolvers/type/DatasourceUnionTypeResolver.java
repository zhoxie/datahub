package com.linkedin.datahub.graphql.resolvers.type;

import com.linkedin.datahub.graphql.generated.HiveSource;
import com.linkedin.datahub.graphql.generated.IcebergSource;
import com.linkedin.datahub.graphql.generated.KafkaMetadataSource;
import com.linkedin.datahub.graphql.generated.MysqlSource;
import com.linkedin.datahub.graphql.generated.OracleSource;
import com.linkedin.datahub.graphql.generated.PinotSource;
import com.linkedin.datahub.graphql.generated.PostgresSource;
import com.linkedin.datahub.graphql.generated.PrestoSource;
import com.linkedin.datahub.graphql.generated.TiDBSource;
import com.linkedin.datahub.graphql.generated.TrinoSource;
import com.linkedin.datahub.graphql.generated.SnowflakeSource;
import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class DatasourceUnionTypeResolver implements TypeResolver {

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
        if (env.getObject() instanceof IcebergSource) {
            return env.getSchema().getObjectType("IcebergSource");
        } else if (env.getObject() instanceof KafkaMetadataSource) {
            return env.getSchema().getObjectType("KafkaMetadataSource");
        } else if (env.getObject() instanceof MysqlSource) {
            return env.getSchema().getObjectType("MysqlSource");
        } else if (env.getObject() instanceof OracleSource) {
            return env.getSchema().getObjectType("OracleSource");
        } else if (env.getObject() instanceof PostgresSource) {
            return env.getSchema().getObjectType("PostgresSource");
        } else if (env.getObject() instanceof TiDBSource) {
            return env.getSchema().getObjectType("TiDBSource");
        } else if (env.getObject() instanceof HiveSource) {
            return env.getSchema().getObjectType("HiveSource");
        } else if (env.getObject() instanceof PinotSource) {
            return env.getSchema().getObjectType("PinotSource");
        } else if (env.getObject() instanceof PrestoSource) {
            return env.getSchema().getObjectType("PrestoSource");
        } else if (env.getObject() instanceof TrinoSource) {
            return env.getSchema().getObjectType("TrinoSource");
        } else if (env.getObject() instanceof SnowflakeSource) {
            return env.getSchema().getObjectType("SnowflakeSource");
        } else {
            throw new RuntimeException("Unrecognized object type provided to type resolver");
        }
    }
}
