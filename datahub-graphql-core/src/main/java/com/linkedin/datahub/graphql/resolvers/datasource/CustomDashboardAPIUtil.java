package com.linkedin.datahub.graphql.resolvers.datasource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    static {
        Map<String, String> map = new HashMap<>();
        map.put(CreateDatasourceResolver.POSTGRES_SOURCE_NAME, "org.postgresql.Driver");
        map.put(CreateDatasourceResolver.ORACLE_SOURCE_NAME, "oracle.jdbc.driver.OracleDriver");
        map.put(CreateDatasourceResolver.MYSQL_SOURCE_NAME, "com.mysql.cj.jdbc.Driver");

        JDBC_DRIVER = Collections.unmodifiableMap(map);
    }

    public static String buildCreateRequestBody(Map<String, Object> inputMap) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("flag", true);
        json.put("name", (String) inputMap.get("name"));
        String primaryType;
        Map<String, Object> primaryConnMap = (Map<String, Object>) inputMap.get("primaryConn");
        ObjectNode primaryNode = mapper.createObjectNode();
        if (primaryConnMap.containsKey(CreateDatasourceResolver.POSTGRES_SOURCE_NAME)) {
            primaryType = CreateDatasourceResolver.POSTGRES_SOURCE_NAME;
            Map<String, Object> postgresMap = (Map<String, Object>) primaryConnMap.get(CreateDatasourceResolver.POSTGRES_SOURCE_NAME);
            String hostPort = (String) postgresMap.get("hostPort");
            String database = (String) postgresMap.get("database");
            String url =  "jdbc:postgresql://" + hostPort + "/" + database;
            primaryNode.put("username", (String) postgresMap.get("username"));
            primaryNode.put("password", (String) postgresMap.get("password"));
            primaryNode.put("url", url);

        } else if (primaryConnMap.containsKey(CreateDatasourceResolver.ORACLE_SOURCE_NAME)) {
            primaryType = CreateDatasourceResolver.ORACLE_SOURCE_NAME;
            Map<String, Object> oracleMap = (Map<String, Object>) primaryConnMap.get(CreateDatasourceResolver.ORACLE_SOURCE_NAME);
            String hostPort = (String) oracleMap.get("hostPort");
            String database;
            if (oracleMap.containsKey("database")) {
                database = (String) oracleMap.get("database");
            } else {
                database = (String) oracleMap.get("serviceName");
            }
            String url =  " jdbc:oracle:thin:@" + hostPort + ":" + database;
            primaryNode.put("username", (String) oracleMap.get("username"));
            primaryNode.put("password", (String) oracleMap.get("password"));
            primaryNode.put("url", url);
        } else if (primaryConnMap.containsKey(CreateDatasourceResolver.MYSQL_SOURCE_NAME)) {
            primaryType = CreateDatasourceResolver.MYSQL_SOURCE_NAME;
            Map<String, Object> mysqlMap = (Map<String, Object>) primaryConnMap.get(CreateDatasourceResolver.MYSQL_SOURCE_NAME);
            String hostPort = (String) mysqlMap.get("hostPort");
            String database = (String) mysqlMap.get("database");
            String url =  "jdbc:mysql://" + hostPort + "/" + database;
            primaryNode.put("username", (String) mysqlMap.get("username"));
            primaryNode.put("password", (String) mysqlMap.get("password"));
            primaryNode.put("url", url);
        } else {
            throw new IllegalArgumentException("the source type not supported.");
        }

        primaryNode.put("cluster", "PRIMARY");
        primaryNode.put("driver", JDBC_DRIVER.get(primaryType));
        ArrayNode dataSources = mapper.createArrayNode();
        dataSources.add(primaryNode);


        boolean hasGSB = inputMap.containsKey("gsbConn");
        if (hasGSB) {
            ObjectNode gsbNode = mapper.createObjectNode();
            Map<String, Object> gsbConnMap = (Map<String, Object>) inputMap.get("gsbConn");
            String gsbType;
            if (gsbConnMap.containsKey(CreateDatasourceResolver.POSTGRES_SOURCE_NAME)) {
                gsbType = CreateDatasourceResolver.POSTGRES_SOURCE_NAME;
                Map<String, Object> postgresMap = (Map<String, Object>) gsbConnMap.get(CreateDatasourceResolver.POSTGRES_SOURCE_NAME);
                String hostPort = (String) postgresMap.get("hostPort");
                String database = (String) postgresMap.get("database");
                String url =  "jdbc:postgresql://" + hostPort + "/" + database;
                gsbNode.put("username", (String) postgresMap.get("username"));
                gsbNode.put("password", (String) postgresMap.get("password"));
                gsbNode.put("url", url);
            } else if (gsbConnMap.containsKey(CreateDatasourceResolver.ORACLE_SOURCE_NAME)) {
                gsbType = CreateDatasourceResolver.ORACLE_SOURCE_NAME;
                Map<String, Object> oracleMap = (Map<String, Object>) gsbConnMap.get(CreateDatasourceResolver.ORACLE_SOURCE_NAME);
                String hostPort = (String) oracleMap.get("hostPort");
                String database;
                if (oracleMap.containsKey("database")) {
                    database = (String) oracleMap.get("database");
                } else {
                    database = (String) oracleMap.get("serviceName");
                }
                String url =  " jdbc:oracle:thin:@" + hostPort + ":" + database;
                gsbNode.put("username", (String) oracleMap.get("username"));
                gsbNode.put("password", (String) oracleMap.get("password"));
                gsbNode.put("url", url);
            } else if (gsbConnMap.containsKey(CreateDatasourceResolver.MYSQL_SOURCE_NAME)) {
                gsbType = CreateDatasourceResolver.MYSQL_SOURCE_NAME;
                Map<String, Object> mysqlMap = (Map<String, Object>) gsbConnMap.get(CreateDatasourceResolver.MYSQL_SOURCE_NAME);
                String hostPort = (String) mysqlMap.get("hostPort");
                String database = (String) mysqlMap.get("database");
                String url =  "jdbc:mysql://" + hostPort + "/" + database;
                gsbNode.put("username", (String) mysqlMap.get("username"));
                gsbNode.put("password", (String) mysqlMap.get("password"));
                gsbNode.put("url", url);
            } else {
                throw new IllegalArgumentException("the source type not supported.");
            }
            if (!gsbType.equals(primaryType)) {
                throw new IllegalArgumentException("GSB type was different from primary type.");
            }

            gsbNode.put("cluster", "GSB");
            gsbNode.put("driver", JDBC_DRIVER.get(primaryType));
            dataSources.add(gsbNode);
        }
        json.put("type", primaryType);
        json.put("category", (String) inputMap.get("category"));
        json.put("region", (String) inputMap.get("region"));
        json.put("dataSources", dataSources);

        return json.toString();
    }

    private static String accessToken = null;
    private static long expiresTimestamp = 0;
    public static synchronized String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() > expiresTimestamp) {
            String machineAccountPass = new String(Base64Utils.decodeFromString(System.getenv("MATS_CI_MACHINE_ACCOUNT_PASS")));
            String bearerToken = getBearerToken(System.getenv("MATS_CI_ORG_ID"), System.getenv("MATS_CI_MACHINE_ACCOUNT_NAME"),
                    machineAccountPass);
            Map<String, Object> map = getAccessTokenObj(bearerToken, System.getenv("MATS_CI_CLIENT_ID"),
                    System.getenv("MATS_CI_CLIENT_PASS"), System.getenv("MATS_CI_SCOPE"));
            accessToken = (String) map.get("access_token");
            expiresTimestamp = System.currentTimeMillis() + ((int) map.get("expires_in") - 600) * 1000;
        }
        return accessToken;
    }

    private static String getBearerToken(String orgId, String machineAccountName, String machineAccountPass) {
        String url = System.getenv("MATS_CI_HOST") + "/idb/token/" + orgId + "/v2/actions/GetBearerToken/invoke";
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(
                new StringEntity("{\"name\":\"" + machineAccountName + "\",\"password\":\"" + machineAccountPass + "\"}",
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
        String url = System.getenv("MATS_CI_HOST") + "/idb/oauth2/v1/access_token";
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
