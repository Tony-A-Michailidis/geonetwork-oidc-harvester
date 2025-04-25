package org.fao.geonet.kernel.harvest.harvester.oidc;

import org.fao.geonet.kernel.*;
import org.jdom.Element;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MetadataFetcher {
    public static void fetchAndIngest(String endpoint, String token, ServiceContext context) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int status = conn.getResponseCode();
        if (status != 200) throw new RuntimeException("Failed to fetch metadata: HTTP " + status);

        String xml = new Scanner(conn.getInputStream()).useDelimiter("\A").next();
        Element metadata = Xml.loadString(xml, false);
        DataManager dataMan = context.getBean(DataManager.class);
        dataMan.insertMetadata(context, "uuid-generated", metadata, null, "user-id", "0", null, null);
    }
}
