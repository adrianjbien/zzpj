package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Adzuna {
    private static final String ROOT_JOB_API = "https://api.adzuna.com/v1/api/jobs/";

    private final String appId;
    private final String appKey;
    private final HttpClient client;

    public Adzuna() {
        PropertyReader pr = new PropertyReader();
        try {
            this.appId = pr.readPropertyFile("secret.properties", "app_id");
            this.appKey = pr.readPropertyFile("secret.properties", "app_key");
            this.client = HttpClient.newHttpClient();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read API keys", e);
        }
    }

    private String buildUrl(String countryCode, int page) {
        return ROOT_JOB_API + countryCode + "/search/" + page + "?app_id=" + appId + "&app_key=" + appKey;
    }

    private String getResponse(String countryCode, int page) {
        String finalUrl = buildUrl(countryCode, page);
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(finalUrl)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("API returned status code " + response.statusCode());
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching jobs from Adzuna API", e);
        }
    }

    public String getJobs() {
        String jsonResponse = getResponse("pl", 1);
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray resultsArray = jsonObject.getJSONArray("results");
        return resultsArray.toString();
    }
}
