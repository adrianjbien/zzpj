package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public String getITJobs(int pages) throws JsonProcessingException {
        var countryCodes = new String[] {
                "us", "at", "au", "be", "br", "ca", "ch", "de", "es", "fr",
                "in", "it", "nl", "mx", "nz", "pl", "sg", "za", "gb"
        };



        JSONArray finalResults = new JSONArray();

        for (var countryCode : countryCodes) {
            for (var page = 1; page <= pages; page++) {
                String jsonResponse = getResponse(countryCode, page);
                if (jsonResponse == null) continue;

                JSONObject jsonObject = new JSONObject(jsonResponse);

                if (!jsonObject.has("results")) continue;

                JSONArray resultsArray = jsonObject.getJSONArray("results");

                for (var i = 0; i < resultsArray.length(); i++) {
                    JSONObject job = resultsArray.getJSONObject(i);

                    JSONObject category = job.optJSONObject("category");
                    String categoryLabel = category != null ? category.optString("label", "").toLowerCase() : "";
                    String categoryTag = category != null ? category.optString("tag", "").toLowerCase() : "";

                    boolean isIT = JobUtils.isITJob(categoryLabel, categoryTag);

                    if (!isIT) continue;

                    JSONObject simplifiedJob = new JSONObject();
                    simplifiedJob.put("title", job.optString("title", "Not Found"));
                    simplifiedJob.put("location", job.optJSONObject("location") != null
                            ? job.getJSONObject("location").optString("display_name", "Not Found")
                            : "Not Found");
                    simplifiedJob.put("company", job.optJSONObject("company") != null
                            ? job.getJSONObject("company").optString("display_name", "Not Found")
                            : "Not Found");
                    simplifiedJob.put("description", job.optString("description", "Not Found"));
                    simplifiedJob.put("salary_min", job.opt("salary_min"));
                    simplifiedJob.put("salary_max", job.opt("salary_max"));
                    simplifiedJob.put("contract_type", job.optString("contract_type", "Not Found"));
                    simplifiedJob.put("category_label", categoryLabel);
                    simplifiedJob.put("category_tag", categoryTag);
                    simplifiedJob.put("redirect_url", job.optString("redirect_url", ""));

                    finalResults.put(simplifiedJob);
                }
            }
        }

        JsonFormatter formatter = new JsonFormatter();
        try {
            return formatter.getPrettyJson(finalResults.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to format JSON", e);
        }
    }


}
