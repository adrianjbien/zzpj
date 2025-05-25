package org.example;

import java.util.Arrays;
import java.util.List;

public class JobUtils {

    private static final List<String> IT_KEYWORDS = Arrays.asList(
            "it", "tech", "software", "developer", "engineering", "programming", "data",
            "machine learning", "ai", "artificial intelligence", "cybersecurity", "cloud",
            "devops", "qa", "testing", "support", "network", "administrator", "system",
            "web", "full stack", "backend", "frontend", "mobile", "blockchain", "ml",
            "security", "ios", "android"
    );

    public static boolean isITJob(String label, String tag) {
        String combinedText = (label + " " + tag).toLowerCase();

        // separate words by spaces, commas
        List<String> words = Arrays.asList(combinedText.split("\\W+"));

        return IT_KEYWORDS.stream().anyMatch(words::contains);
    }


}

