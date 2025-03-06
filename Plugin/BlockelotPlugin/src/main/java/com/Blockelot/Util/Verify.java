package com.Blockelot.Util;

import com.Blockelot.Tools;
import com.Blockelot.PluginManager;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bukkit.ChatColor;

public class Verify {

    public static void Register( Tools Plugin) {

        String uri = PluginManager.Config.BaseUri + "Version?version='" + PluginManager.Version + "'"
                + "&worldId=" + PluginManager.Config.WorldId
                + "&serverName='" + Plugin.getServer().getName() + "'";

        ServerUtil.consoleLog("Calling... '" + uri + "'");
        String body = RequestHttp(uri);

        ServerUtil.consoleLog("Response: " + body );
        ServerUtil.consoleLog("#########################################################");
        String[] parts = body.split("\\|");
        String WorldId = parts[0];
        String MinimumVersion = parts[1];
        ServerUtil.consoleLog("Minimum Local Version: " + MinimumVersion);
        ServerUtil.consoleLog("Current Local Version: " + PluginManager.Version);
        ServerUtil.consoleLog("WorldId: " + WorldId);
        if (!WorldId.equalsIgnoreCase(PluginManager.Config.WorldId)) {
            PluginManager.Config.WorldId = WorldId;
            PluginManager.Config.SaveData();
        }

        if (!MinimumVersion.equalsIgnoreCase(PluginManager.Version)) {
            ServerUtil.consoleLog("Blockelot is out of Date!");
        }

        ServerUtil.consoleLog(ChatColor.BLUE + "-----------------BLOCKELOT-ABOUT---------------------");
        ServerUtil.consoleLog(ChatColor.YELLOW + "BLOCKELOT");
        ServerUtil.consoleLog(ChatColor.YELLOW + "Programmed by: Vince Gee a.k.a. ChapleKeep");
        ServerUtil.consoleLog(ChatColor.YELLOW + "Website: www.blockelot.com");
        ServerUtil.consoleLog(ChatColor.YELLOW + "Patreon: https://www.patreon.com/Blockelot");
        ServerUtil.consoleLog(ChatColor.YELLOW + "Email: Vince@Fairfieldtek.com");
        ServerUtil.consoleLog(ChatColor.YELLOW + "");
        ServerUtil.consoleLog(ChatColor.YELLOW + "Blockelot and it's Cloud Storage is provided \"as is\", without warranties of any kind.");
        ServerUtil.consoleLog(ChatColor.YELLOW + "");

        ServerUtil.consoleLog("#########################################################");

    }

    private static String RequestHttp(String uri) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(uri);
            request.addHeader("content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).setConnectionRequestTimeout(120000).build();
            request.setConfig(requestConfig);
            CloseableHttpResponse result = httpClient.execute(request);
            return EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (Exception e) {
            ServerUtil.consoleLog(e.getLocalizedMessage());
            ServerUtil.consoleLog(e.getMessage());
        }
        return null;
    }
}
