package org.iru.translation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.iru.translation.model.PropertiesManager;

public class PreferencesManager {
    public final static String FILES_DIRECTORY = "files.directory";
    
    PropertiesManager propertiesManager = new PropertiesManager();
    Properties applicationProperties;
    
    public Properties loadPreferences() throws TranslationException {
        File f = new File("translations-preferences.conf");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return new Properties();
            }
        }
        applicationProperties = propertiesManager.readProperties(new File("translations-preferences.conf"));
        return applicationProperties;
    }
    
    public void savePreferences() throws TranslationException {
        try {
            applicationProperties.store(new FileOutputStream(new File("translations-preferences.conf")), "Translation Application Preferences");
        } catch (IOException ex) {
            throw new TranslationException("Unable to save preferences", ex);
        }
    }

    public void setPreference(String key, String value) {
        applicationProperties.setProperty(key, value);
    }

    public String getPreference(String key) {
        return applicationProperties.getProperty(key);
    }
    
}
