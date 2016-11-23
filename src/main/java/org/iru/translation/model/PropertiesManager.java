package org.iru.translation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.iru.translation.TranslationException;
import org.iru.translation.gui.Action;
import org.iru.translation.model.PropertyTableModel.Property;

public class PropertiesManager {
    
    public Properties readProperties(File f) throws TranslationException {
        Properties p = new Properties();
        FileInputStream fileInputStream = null;
        InputStreamReader reader = null;
        try {
            fileInputStream = new FileInputStream(f);
            reader = new InputStreamReader(fileInputStream, "UTF-8");
            p.load(reader);
        } catch (IOException ex) {
            throw new TranslationException("Unable to parse file", ex);
        } finally {
            try {reader.close();} catch(Exception ex) {};
            try {fileInputStream.close();} catch(Exception ex) {};
        }
        return p;
    }
    
    public List<Property> loadProperties(Properties props) {
        List<PropertyTableModel.Property> result = new LinkedList<>();
        props.entrySet().stream()
                .forEach(p -> {
                    result.add(new PropertyTableModel.Property((String) p.getKey(), (String) p.getValue(), null, Action.NONE));
                });
        Collections.sort(result);
        return result;
    }

    public List<Property> diff(Properties fromProps, Properties toProps) {
        List<Property> result = new LinkedList<>();
        Set<Object> insertedkeys = new HashSet<>(fromProps.size());
        fromProps.entrySet().stream()
            .forEach(p -> {
                String toValueAsString = (String)toProps.get(p.getKey());
                final String fromValueAsString = (String)p.getValue();
                if (toValueAsString == null) {
                    result.add(new Property((String)p.getKey(), fromValueAsString, null, Action.DELETED));
                } else if (fromValueAsString.trim().equalsIgnoreCase(toValueAsString.trim())) {
                    result.add(new Property((String)p.getKey(), (String)p.getValue(), toValueAsString, Action.UNTRANSLATED));
                } else if (!insertedkeys.contains(p.getKey())) {
                    insertedkeys.add(p.getKey());
                    result.add(new Property((String)p.getKey(), fromValueAsString, toValueAsString, Action.NONE));
                }
            });
        toProps.entrySet().stream()
            .forEach(p -> {
                String fromValueAsString = (String)fromProps.get(p.getKey());
                if (fromValueAsString == null) {
                    result.add(new Property((String)p.getKey(), null, (String)p.getValue(), Action.ADDED));
                }
            });
        Collections.sort(result);
        return result;
    }
}
