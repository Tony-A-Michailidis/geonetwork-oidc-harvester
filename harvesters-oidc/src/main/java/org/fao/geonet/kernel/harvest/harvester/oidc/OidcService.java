//Here's exactly what you need to do to manually add refresh token handling to your custom OIDC harvester Java class. 

package org.fao.geonetwork.harvest.harvester.oidc;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OidcService {

    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String password;
    private final String tokenEndpoint;

    private String accessToken;
    private String refreshToken;
    private long tokenExpiryTime;

    public OidcService(String clientId, String clientSecret, String username, String password, String tokenEndpoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getAccessToken() throws IOException {
        if (accessToken == null || System.currentTimeMillis() > tokenExpiryTime) {
            if (refreshToken != null) {
                try {
                    refreshAccessToken();
                    return accessToken;
                } catch (IOException e) {
                    System.out.println("Refresh token failed: " + e.getMessage());
                    // fallback to full login
                }
            }
            fetchAccessToken();
        }
        return accessToken;
    }

    private void fetchAccessToken() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(tokenEndpoint);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(responseBody);
                accessToken = json.getString("access_token");
                refreshToken = json.optString("refresh_token", null);
                int expiresIn = json.optInt("expires_in", 300);
                tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000) - 5000;
            }
        }
    }

    private void refreshAccessToken() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(tokenEndpoint);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            params.add(new BasicNameValuePair("refresh_token", refreshToken));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(responseBody);
                accessToken = json.getString("access_token");
                refreshToken = json.optString("refresh_token", refreshToken);
                int expiresIn = json.optInt("expires_in", 300);
                tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000) - 5000;
            }
        }
    }
}
