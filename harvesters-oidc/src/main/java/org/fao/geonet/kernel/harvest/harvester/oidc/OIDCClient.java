package org.fao.geonet.kernel.harvest.harvester.oidc;

import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class OIDCClient {
    private final String tokenEndpoint, clientId, clientSecret, username, password;
    private String refreshToken;
    private static final Logger log = Logger.getLogger(OIDCClient.class.getName());

    public OIDCClient(String tokenEndpoint, String clientId, String clientSecret, String username, String password) {
        this.tokenEndpoint = tokenEndpoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
    }

    public String getAccessToken() throws IOException {
        URL url = new URL(tokenEndpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        String body = "grant_type=password" +
                      "&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                      "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
                      "&username=" + URLEncoder.encode(username, "UTF-8") +
                      "&password=" + URLEncoder.encode(password, "UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status != 200) throw new IOException("Failed to get token: HTTP " + status);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String result = reader.lines().reduce("", (acc, line) -> acc + line);
            JSONObject json = new JSONObject(result);
            refreshToken = json.optString("refresh_token", null);
            return json.getString("access_token");
        }
    }
}
