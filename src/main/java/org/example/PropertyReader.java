package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    public String readPropertyFile(String filePath, String propName) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(filePath));
        return prop.getProperty(propName);
    }
}
