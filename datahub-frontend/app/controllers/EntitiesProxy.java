package controllers;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntitiesProxy extends Controller {

    private final Logger _logger = LoggerFactory.getLogger(EntitiesProxy.class.getName());


    public Result entitiesProxy() {

        String gmsHost = System.getenv("DATAHUB_GMS_HOST") == null ? "localhost" : System.getenv("DATAHUB_GMS_HOST");
        String gmsPort = System.getenv("DATAHUB_GMS_PORT") == null ? "8080" : System.getenv("DATAHUB_GMS_PORT");

        Map<String, String[]> orgParamsMap = request().queryString();
        String params = "";
        for(String key : orgParamsMap.keySet()){
            String[] values = orgParamsMap.get(key);
            for(String value : values){
                params += key+"="+value+"&";
            }
        }
        params = params.substring(0, params.length() - 1);

        String url = "http://"+gmsHost+":"+gmsPort+request().path()+"?"+params;


        HttpPost post = new HttpPost(url);
        Map<String, List<String>> headerMap = request().getHeaders().toMap();
        for(String key : headerMap.keySet()){
            if(key.equalsIgnoreCase("Content-Length")){
                continue;
            }
            List<String> values = headerMap.get(key);
            for(String value : values){
                post.setHeader(new BasicHeader(key, value));
            }
        }

        if(request().hasBody()){
            ContentType contentType = ContentType.getByMimeType(headerMap.getOrDefault("Content-Type", Collections.singletonList("application/json")).get(0));
            post.setEntity(new StringEntity(request().body().asBytes().utf8String(), contentType));
        }

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse resp = httpClient.execute(post)
        ){
            if(resp.getStatusLine().getStatusCode() == 200){
                return ok(EntityUtils.toString(resp.getEntity()));
            } else {
                return status(resp.getStatusLine().getStatusCode(), EntityUtils.toString(resp.getEntity()));
            }
        }  catch (IOException ioe){
            _logger.error(ioe.getMessage(), ioe);
            return status(500, ioe.getMessage());
        }
    }
}
