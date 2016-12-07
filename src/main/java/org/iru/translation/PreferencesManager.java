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
    
    public void loadPreferences() throws PreferencesException {
        File f = new File("translations-preferences.conf");
        if (!f.exists()) {
            applicationProperties = new Properties();
        } else {
            try {
                applicationProperties = propertiesManager.readProperties(new File("translations-preferences.conf"));
            } catch (TranslationException ex) {
                throw new PreferencesException("Unable to load preferences", ex);
            }
        }
    }
    
    public void savePreferences() throws PreferencesException {
        File f = new File("translations-preferences.conf");
        if (applicationProperties.isEmpty()) {
            return;
        }
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            applicationProperties.store(new FileOutputStream(f), "Translation Application Preferences");
        } catch (IOException ex) {
            throw new PreferencesException("Unable to save preferences", ex);
        }
    }

    public void setPreference(String key, String value) {
        applicationProperties.setProperty(key, value);
    }

    public String getPreference(String key) {
        return applicationProperties.getProperty(key);
    }
    
}
