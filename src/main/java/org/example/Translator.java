package org.example;

import com.deepl.api.DeepLClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Translator {
    private DeepLClient client;

    public Translator() {
        String authKey = loadAuthKey();
        if (authKey == null || authKey.isEmpty()) {
            throw new RuntimeException("Auth key is missing or invalid!");
        }
        client = new DeepLClient(authKey);
    }

    private String loadAuthKey() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("api_key.properties")) {
            props.load(input);
            return props.getProperty("auth_key");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
