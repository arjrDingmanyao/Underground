package com.creditcloud.config.impl;

import com.creditcloud.config.ConfigLoader;
import com.creditcloud.model.qualifier.Local;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.enterprise.context.ApplicationScoped;

@Local
@ApplicationScoped
public class LocalConfigLoader
        implements ConfigLoader {

    private static final String LOCAL_CONFIG_REPO = "/var/CreditCloud/config/";

    public Reader loadConfig(String configName)
            throws IOException {
        String configFileName = getConfigFilename(configName);
        File configFile = new File(configFileName);
        if ((configFile.exists()) && (configFile.canRead())) {
            return new FileReader(configFile);
        }
        throw new FileNotFoundException(configFileName);
    }

    public long getLastModified(String configName) {
        String configFileName = getConfigFilename(configName);
        File configFile = new File(configFileName);
        if ((configFile.exists()) && (configFile.canRead())) {
            return configFile.lastModified();
        }
        return -1L;
    }

    private String getConfigFilename(String configName) {
        return "/var/CreditCloud/config/" + configName + ".xml";
    }
}
