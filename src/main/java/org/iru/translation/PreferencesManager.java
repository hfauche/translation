package org.iru.translation;

import java.io.File;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.iru.translation.model.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferencesManager {
    private final static Logger logger = LoggerFactory.getLogger(PreferencesManager.class);

    public final static String FILES_DIRECTORY = "files.directory";
    
    private final PropertiesManager propertiesManager;
    private FileBasedConfigurationBuilder<FileBasedConfiguration> applicationPropertiesBuilder;
    private Configuration applicationConfiguration;
    private final String preferencesFileLocation = System.getProperty("user.home") + "/translations-preferences.conf";

    public PreferencesManager(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }
    
    public void loadPreferences() {
        try {
            applicationPropertiesBuilder = propertiesManager.getPropertiesBuilder(new File(preferencesFileLocation));
            applicationConfiguration = applicationPropertiesBuilder.getConfiguration();
        } catch (ConfigurationException | TranslationException ex) {
            logger.warn("Exception raised when loading preferences", ex);
        }
    }
    
    public void savePreferences() {
        try {
            applicationPropertiesBuilder.save();
        } catch (ConfigurationException ex) {
            logger.warn("Exception raised when saving preferences", ex);
        }
    }

    public void setPreference(String key, String value) {
        if (applicationConfiguration != null) {
            applicationConfiguration.setProperty(key, value);
        }
    }

    public String getPreference(String key) {
        if (applicationConfiguration != null) {
            return applicationConfiguration.getString(key);
        } else {
            return null;
        }
    }
    
}
