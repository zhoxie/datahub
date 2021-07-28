package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatasourceTestConn extends Controller {

    public Result testConn() throws IOException {

        if(request().hasBody()){

            JsonNode json = request().body().asJson();
            String type = json.get("type").textValue();
            String driver = json.get("driver").textValue();
            String url = json.get("url").textValue();
            String username = json.get("username").textValue();
            String password = json.get("password").textValue();

            if("mysql".equalsIgnoreCase(type)){
                return mysqlTest(driver, url, username, password);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode j = mapper.readTree("{\"result\":400,\"message\":\""+type+" not support yet.\"}");
                return status(400, j);
            }


        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree("{\"result\":400,\"message\":\"not a valid params.\"}");
            return status(400, json);
        }
    }

    private Result mysqlTest(String driver, String url, String username, String password) throws IOException {
            try {
                Class.forName(driver);

            } catch (ClassNotFoundException e){
                ObjectMapper mapper = new ObjectMapper();
                JsonNode j = mapper.readTree("{\"result\":400,\"message\":\"driver class not found.\"}");
                return status(400, j);
            }

            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                PreparedStatement prstat =conn.prepareStatement("select 1+1");
                ResultSet rs = prstat.executeQuery();
                if(rs.next()  && rs.getInt(1) == 2){
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode j = mapper.readTree("{\"result\":200,\"message\":\"success.\"}");
                    return ok(j);
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode j = mapper.readTree("{\"result\":500,\"message\":\"failed.\"}");
                    return status(500, j);
                }
            } catch (SQLException e){
                ObjectMapper mapper = new ObjectMapper();
                JsonNode j = mapper.readTree("{\"result\":500,\"message\":\""+e.getMessage()+"\"}");
                return status(500, j);
            }




    }

}
