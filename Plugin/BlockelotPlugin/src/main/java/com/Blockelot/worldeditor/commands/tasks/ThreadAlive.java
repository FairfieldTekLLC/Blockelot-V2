package com.Blockelot.worldeditor.commands.tasks;

import com.Blockelot.Configuration;
import com.Blockelot.PluginManager;
import com.Blockelot.Util.ServerUtil;
import com.Blockelot.worldeditor.container.PlayerInfo;
import com.Blockelot.worldeditor.http.SchematicDataDownloadRequest;
import com.google.gson.Gson;
import java.io.IOException;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author geev
 */
public class ThreadAlive implements Runnable {

    private final StringObject StringObject;

    public ThreadAlive(PlayerInfo pi, String fileName, StringObject obj) {
        PlayerInfo = pi;
        Filename = fileName;
        StringObject = obj;
    }

    public String RequestHttp(String uri, String postBody) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            StringEntity params = new StringEntity(postBody);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).setConnectionRequestTimeout(120000).build();
            request.setConfig(requestConfig);
            System.out.println("------------------------------------> Making Web Request! ");
            CloseableHttpResponse result = httpClient.execute(request);
            return EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (IOException | ParseException e) {
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
        }
        return null;
    }

    private void Fetch() {
        SchematicDataDownloadRequest req = new SchematicDataDownloadRequest();
        req.setAuth(PlayerInfo.getLastAuth());
        req.setCurrentDirectory(PlayerInfo.getCurrentPath());
        req.setFileName(this.Filename);
        req.setUuid(PlayerInfo.getUUID());
        Gson gson = new Gson();
        String body = gson.toJson(req);
        System.out.println("------------------------------------> Result: " + body);
        StringObject.setString(RequestHttp(Configuration.BaseUri + "Load", body));
    }

    private PlayerInfo PlayerInfo = null;
    private String Filename = "";

    @Override
    public void run() {
        Fetch();
    }

}
