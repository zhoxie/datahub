package com.linkedin.datahub.graphql.resolvers.type;

import com.linkedin.datahub.graphql.generated.IcebergSource;
import com.linkedin.datahub.graphql.generated.KafkaMetadataSource;
import com.linkedin.datahub.graphql.generated.MysqlSource;
import com.linkedin.datahub.graphql.generated.OracleSource;
import com.linkedin.datahub.graphql.generated.PostgresSource;
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
        } else {
            throw new RuntimeException("Unrecognized object type provided to type resolver");
        }
    }
}
