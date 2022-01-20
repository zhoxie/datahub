package com.linkedin.datahub.graphql.resolvers.datasource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linkedin.util.Configuration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomDashboardAPIUtil {

    private CustomDashboardAPIUtil() {
    }

    static final Map<String, String> JDBC_DRIVER;
    static final Map<String, String> JDBC_TYPE;
    static {
        Map<String, String> map = new HashMap<>();
        map.put(DatasourceConstants.POSTGRES_SOURCE_NAME, "org.postgresql.Driver");
        map.put(DatasourceConstants.ORACLE_SOURCE_NAME, "oracle.jdbc.OracleDriver");
        map.put(DatasourceConstants.TIDB_SOURCE_NAME, "com.mysql.jdbc.Driver");
        map.put(DatasourceConstants.MYSQL_SOURCE_NAME, "com.mysql.jdbc.Driver");
        map.put(DatasourceConstants.HIVE_SOURCE_NAME, "org.apache.hive.jdbc.HiveDriver");
        map.put(DatasourceConstants.PRESTO_SOURCE_NAME, "com.facebook.presto.jdbc.PrestoDriver");
        map.put(DatasourceConstants.PINOT_SOURCE_NAME, "org.apache.pinot.client.PinotDriver");
        map.put(DatasourceConstants.TRINO_SOURCE_NAME, "io.trino.jdbc.TrinoDriver");
        JDBC_DRIVER = Collections.unmodifiableMap(map);

        map = new HashMap<>();
        map.put(DatasourceConstants.POSTGRES_SOURCE_NAME, "postgresql");
        map.put(DatasourceConstants.ORACLE_SOURCE_NAME, "oracle:thin");
        map.put(DatasourceConstants.TIDB_SOURCE_NAME, "mysql");
        map.put(DatasourceConstants.MYSQL_SOURCE_NAME, "mysql");
        map.put(DatasourceConstants.HIVE_SOURCE_NAME, "hive2");
        map.put(DatasourceConstants.PRESTO_SOURCE_NAME, "presto");
        map.put(DatasourceConstants.PINOT_SOURCE_NAME, "pinot");
        map.put(DatasourceConstants.TRINO_SOURCE_NAME, "trino");
        JDBC_TYPE = Collections.unmodifiableMap(map);

    }

    public static String buildTestRequestBody(String type, Map<String, Object> dbMap) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("type", type.equals(DatasourceConstants.POSTGRES_SOURCE_NAME) ? "postgresql" : type);
        json.put("driver", JDBC_DRIVER.get(type));
        json.put("username", (String) dbMap.get("username"));
        json.put("passphase", (String) dbMap.get("password"));
        json.put("url", parseJDBCURL(type, dbMap));
        return json.toString();
    }

    public static String parseJDBCURL(String type, Map<String, Object> dbMap) {
        String jdbcUrl;

        //pinot
        if (DatasourceConstants.PINOT_SOURCE_NAME.equals(type)) {
            jdbcUrl = "jdbc:" + JDBC_TYPE.get(type) + "://" + dbMap.get("hostPort");
            //trino presto
        } else if (DatasourceConstants.TRINO_SOURCE_NAME.equals(type)
                || DatasourceConstants.PRESTO_SOURCE_NAME.equals(type)) {
            jdbcUrl = "jdbc:" + JDBC_TYPE.get(type) + "://" + dbMap.get("hostPort");;
            if (dbMap.containsKey("catalog")) {
                jdbcUrl = jdbcUrl + "/" + dbMap.get("catalog");
                if (dbMap.containsKey("schema")) {
                    jdbcUrl = jdbcUrl + "/" + dbMap.get("schema");
                }
            }
            if (dbMap.containsKey("jdbcParams")) {
                String jdbcParams = (String) dbMap.get("jdbcParams");
                jdbcParams = jdbcParams.startsWith("?") ? jdbcParams : ("?" + jdbcParams);
                jdbcUrl = jdbcUrl + jdbcParams;
            }
            //hive
        } else if (DatasourceConstants.HIVE_SOURCE_NAME.equals(type)) {
            jdbcUrl = "jdbc:" + JDBC_TYPE.get(type) + "://" + dbMap.get("hostPort") + "/" + dbMap.get("database");
            if (dbMap.containsKey("jdbcParams")) {
                String jdbcParams = (String) dbMap.get("jdbcParams");
                jdbcParams = jdbcParams.startsWith(";") ? jdbcParams : (";" + jdbcParams);
                jdbcUrl = jdbcUrl + jdbcParams;
            }
            //postgres TiDB
        } else if (DatasourceConstants.POSTGRES_SOURCE_NAME.equals(type)
                || DatasourceConstants.MYSQL_SOURCE_NAME.equals(type)
                || DatasourceConstants.TIDB_SOURCE_NAME.equals(type)) {
            jdbcUrl = "jdbc:" + JDBC_TYPE.get(type) + "://" + dbMap.get("hostPort") + "/" + dbMap.get("database");
            if (dbMap.containsKey("jdbcParams")) {
                String jdbcParams = (String) dbMap.get("jdbcParams");
                jdbcParams = jdbcParams.startsWith("?") ? jdbcParams : ("?" + jdbcParams);
                jdbcUrl = jdbcUrl + jdbcParams;
            }
            //oracle
        } else if (DatasourceConstants.ORACLE_SOURCE_NAME.equals(type)) {
            jdbcUrl = "jdbc:" + JDBC_TYPE.get(type) + ":@";
            if (dbMap.containsKey("hostPort") && dbMap.containsKey("serviceName")) {
                jdbcUrl = jdbcUrl + "//" + dbMap.get("hostPort") + "/" + dbMap.get("serviceName");
            } else {
                jdbcUrl = jdbcUrl + dbMap.get("tnsName");
            }

        } else {
            throw new IllegalArgumentException("Custom Dashboard not support the type:" + type);
        }

        return jdbcUrl;
    }

    private static void parseJDBCRequestBody(String type, Map<String, Object> dbMap, ObjectNode node) {
        node.put("username", (String) dbMap.get("username"));
        node.put("password", (String) dbMap.get("password"));
        node.put("url", parseJDBCURL(type, dbMap));
    }

    private static String getType(Map<String, Object> connMap) {
        if (connMap.containsKey(DatasourceConstants.POSTGRES_SOURCE_NAME)) {
            return DatasourceConstants.POSTGRES_SOURCE_NAME;
        } else if (connMap.containsKey(DatasourceConstants.HIVE_SOURCE_NAME)) {
            return DatasourceConstants.HIVE_SOURCE_NAME;
        } else if (connMap.containsKey(DatasourceConstants.TIDB_SOURCE_NAME)) {
            return DatasourceConstants.TIDB_SOURCE_NAME;
        } else if (connMap.containsKey(DatasourceConstants.ORACLE_SOURCE_NAME)) {
            return DatasourceConstants.ORACLE_SOURCE_NAME;
        } else if (connMap.containsKey(DatasourceConstants.PINOT_SOURCE_NAME)) {
            return DatasourceConstants.PINOT_SOURCE_NAME;
        } else if (connMap.containsKey(DatasourceConstants.PRESTO_SOURCE_NAME)) {
            return DatasourceConstants.PRESTO_SOURCE_NAME;
        } else if (connMap.containsKey(DatasourceConstants.TRINO_SOURCE_NAME)) {
            return DatasourceConstants.TRINO_SOURCE_NAME;
        } else {
            throw new IllegalArgumentException("the source type not supported.");
        }
    }

    public static boolean supportType(String type) {
        return DatasourceConstants.POSTGRES_SOURCE_NAME.equals(type)
                || DatasourceConstants.HIVE_SOURCE_NAME.equals(type)
                || DatasourceConstants.TIDB_SOURCE_NAME.equals(type)
                || DatasourceConstants.ORACLE_SOURCE_NAME.equals(type)
                || DatasourceConstants.PINOT_SOURCE_NAME.equals(type)
                || DatasourceConstants.PRESTO_SOURCE_NAME.equals(type)
                || DatasourceConstants.TRINO_SOURCE_NAME.equals(type);
    }

    public static String buildCreateRequestBody(Map<String, Object> inputMap) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("flag", true);
        json.put("name", (String) inputMap.get("name"));
        Map<String, Object> primaryConnMap = (Map<String, Object>) inputMap.get("primaryConn");
        ObjectNode primaryNode = mapper.createObjectNode();
        String primaryType = getType(primaryConnMap);
        parseJDBCRequestBody(primaryType, (Map<String, Object>) primaryConnMap.get(primaryType), primaryNode);

        primaryNode.put("cluster", "PRIMARY");
        primaryNode.put("driver", JDBC_DRIVER.get(primaryType));
        ArrayNode dataSources = mapper.createArrayNode();
        dataSources.add(primaryNode);

        boolean hasGSB = inputMap.containsKey("gsbConn");
        if (hasGSB) {
            ObjectNode gsbNode = mapper.createObjectNode();
            Map<String, Object> gsbConnMap = (Map<String, Object>) inputMap.get("gsbConn");
            String gsbType = getType(gsbConnMap);
            parseJDBCRequestBody(gsbType, (Map<String, Object>) gsbConnMap.get(gsbType), gsbNode);

            if (!gsbType.equals(primaryType)) {
                throw new IllegalArgumentException("GSB type was different from primary type.");
            }

            gsbNode.put("cluster", "GSB");
            gsbNode.put("driver", JDBC_DRIVER.get(primaryType));
            dataSources.add(gsbNode);
        }
        json.put("type", primaryType);
        json.put("category", Configuration.getEnvironmentVariable("CUSTOM_DASHBOARD_API_CATEGORY"));
        json.put("region", (String) inputMap.get("region"));
        json.put("dataSources", dataSources);

        return json.toString();
    }

    private static String accessToken = null;
    private static long expiresTimestamp = 0;
    public static synchronized String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() > expiresTimestamp) {
            String machineAccountPass = new String(Base64Utils.decodeFromString(
                    Configuration.getEnvironmentVariable("MATS_CI_MACHINE_ACCOUNT_PASS")
            ));
            String bearerToken = getBearerToken(Configuration.getEnvironmentVariable("MATS_CI_ORG_ID"),
                    Configuration.getEnvironmentVariable("MATS_CI_MACHINE_ACCOUNT_NAME"),
                    machineAccountPass);
            Map<String, Object> map = getAccessTokenObj(bearerToken,
                    Configuration.getEnvironmentVariable("MATS_CI_CLIENT_ID"),
                    Configuration.getEnvironmentVariable("MATS_CI_CLIENT_PASS"),
                    Configuration.getEnvironmentVariable("MATS_CI_SCOPE"));
            accessToken = (String) map.get("access_token");
            expiresTimestamp = System.currentTimeMillis() + ((int) map.get("expires_in") - 600) * 1000;
        }
        return accessToken;
    }

    private static String getBearerToken(String orgId, String machineAccountName, String machineAccountPass) {
        String url = Configuration.getEnvironmentVariable("MATS_CI_HOST") + "/idb/token/"
                + orgId + "/v2/actions/GetBearerToken/invoke";
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(
                new StringEntity("{\"name\":\"" + machineAccountName
                        + "\",\"password\":\"" + machineAccountPass + "\"}",
                        ContentType.APPLICATION_JSON));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse resp = httpClient.execute(post)
        ) {
            if (resp.getStatusLine().getStatusCode() == 200) {
                String resStr = EntityUtils.toString(resp.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, Object> resMap = mapper.readValue(resStr, HashMap.class);
                return (String) resMap.get("BearerToken");
            }
            throw new IllegalStateException("fail to get bearer token.");
        }  catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private static HashMap<String, Object> getAccessTokenObj(String bearerToken, String clientId, String clientPass, String scope) {
        String clientAuth = "Basic " + new String(Base64Utils.encode((clientId.concat(":").concat(clientPass)).getBytes()));
        String url = Configuration.getEnvironmentVariable("MATS_CI_HOST") + "/idb/oauth2/v1/access_token";
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Authorization", clientAuth);
        post.setEntity(
                new StringEntity("grant_type=urn:ietf:params:oauth:grant-type:saml2-bearer&assertion="
                        + bearerToken + "&scope=" + scope + "&self_contained_token=true",
                        "UTF-8"));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse resp = httpClient.execute(post)
        ) {
            if (resp.getStatusLine().getStatusCode() == 200) {
                String resStr = EntityUtils.toString(resp.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(resStr, HashMap.class);
            }
            throw new IllegalStateException("fail to get access token.");
        }  catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
}
