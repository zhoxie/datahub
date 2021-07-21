package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.datahub.graphql.generated.DatasourceCluster;
import com.linkedin.datahub.graphql.generated.DatasourceConnInfo;
import com.linkedin.datahub.graphql.generated.DatasourceConnections;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DatasourceConnectiosMapper implements ModelMapper<com.linkedin.datasource.DatasourceConnections, DatasourceConnections> {

    public static final DatasourceConnectiosMapper INSTANCE = new DatasourceConnectiosMapper();

    public static DatasourceConnections map(@Nonnull final com.linkedin.datasource.DatasourceConnections connections) {
        return INSTANCE.apply(connections);
    }

    @Override
    public DatasourceConnections apply(@Nonnull final com.linkedin.datasource.DatasourceConnections input) {
        final DatasourceConnections result = new DatasourceConnections();

        result.setCategory(input.getCategory());
        result.setDataCenter(input.getDataCenter());

        List<DatasourceConnInfo> conns = new ArrayList<>();
        for (com.linkedin.datasource.DatasourceConnInfo info : input.getConnections()) {
            DatasourceConnInfo dsInfo = new DatasourceConnInfo();
            if (info.getCluster() != null && info.getCluster().isDatasourceCluster()) {
                dsInfo.setCluster(DatasourceCluster.valueOf(info.getCluster().getDatasourceCluster().name()));
            }
            dsInfo.setDriver(info.getDriver());
            dsInfo.setUsername(info.getUsername());
            dsInfo.setPassword(info.getPassword());
            dsInfo.setUrl(info.getUrl());
            conns.add(dsInfo);
        }
        result.setConnections(conns);
        return result;
    }
}
