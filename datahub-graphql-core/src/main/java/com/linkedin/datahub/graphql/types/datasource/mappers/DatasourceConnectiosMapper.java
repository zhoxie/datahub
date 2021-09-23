package com.linkedin.datahub.graphql.types.datasource.mappers;

import com.linkedin.common.urn.DataPlatformUrn;
import com.linkedin.datahub.graphql.generated.DataPlatform;
import com.linkedin.datahub.graphql.generated.DatasourceCluster;
import com.linkedin.datahub.graphql.generated.DatasourceConnInfo;
import com.linkedin.datahub.graphql.generated.DatasourceConnections;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
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
        DataPlatform platform = new DataPlatform();
        DataPlatformUrn platformUrn;
        try {
            platformUrn = DataPlatformUrn.createFromUrn(input.getPlatform());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        platform.setUrn(input.getPlatform().toString());
        platform.setType(EntityType.DATA_PLATFORM);
        platform.setDisplayName(platformUrn.getPlatformNameEntity());
        platform.setName(platformUrn.getPlatformNameEntity());
        result.setPlatform(platform);
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
