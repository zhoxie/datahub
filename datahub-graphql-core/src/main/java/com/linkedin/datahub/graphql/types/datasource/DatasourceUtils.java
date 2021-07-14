package com.linkedin.datahub.graphql.types.datasource;

import com.linkedin.common.urn.DatasourceUrn;

import java.net.URISyntaxException;

public class DatasourceUtils {

    private DatasourceUtils() { }

    static DatasourceUrn getDatasourceUrn(String urnStr) {
        try {
            return DatasourceUrn.createFromString(urnStr);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Failed to retrieve datasource with urn %s, invalid urn", urnStr));
        }
    }
}
