package org.example;

import java.net.http.HttpClient;

public class Adzuna {
    public static final String JOB_API = "https://api.adzuna.com/job/";

    public String getJobs() {
        try (HttpClient client = HttpClient.newHttpClient()) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}