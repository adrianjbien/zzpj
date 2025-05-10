package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class Adzuna {
    public static final String ROOT_JOB_API = "https://api.adzuna.com/v1/api/jobs/";

    private final String APP_KEY;

    private final String APP_ID;

    public Adzuna() {
        PropertyReader pr = new PropertyReader();
        try {
            APP_ID = pr.readPropertyFile("secret.properties", "app_id");
            APP_KEY = pr.readPropertyFile("secret.properties", "app_key");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJobs() {
        String[] countryCodes = new String[] {"gb", "us", "at", "au", "be", "br", "ca", "ch", "de", "es", "fr", "in", "it",
        "nt", "mx", "nz", "pl", "sg", "za"};

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(ROOT_JOB_API)).GET().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}