package org.iru.translation.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.iru.translation.TranslationException;
import org.iru.translation.gui.Action;
import org.iru.translation.properties.PropertyModel.Property;

public class PropertiesManager {
    
    public Properties readProperties(File f) throws TranslationException {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(f));
        } catch (IOException ex) {
            throw new TranslationException("Unable to parse file", ex);
        }
        return p;
    }

    public List<Property> diff(Properties fromProps, Properties toProps) {
        List<Property> result = new LinkedList<>();
        Set<Object> insertedkeys = new HashSet<>(fromProps.size());
        fromProps.entrySet().stream()
            .forEach(p -> {
                String toValueAsString = (String)toProps.get(p.getKey());
                final String fromValueAsString = (String)p.getValue();
                if (toValueAsString == null) {
                    result.add(new Property((String)p.getKey(), fromValueAsString, Action.DELETED));
                } else if (fromValueAsString.trim().equalsIgnoreCase(toValueAsString.trim())) {
                    result.add(new Property((String)p.getKey(), (String)p.getValue(), Action.UNTRANSLATED));
                } else if (!insertedkeys.contains(p.getKey())) {
                    insertedkeys.add(p.getKey());
                    result.add(new Property((String)p.getKey(), (String)p.getValue(), Action.NONE));
                }
            });
        toProps.entrySet().stream()
            .forEach(p -> {
                Object toEntry = fromProps.get(p.getKey());
                if (toEntry == null) {
                    result.add(new Property((String)p.getKey(), (String)p.getValue(), Action.ADDED));
                }
            });
        Collections.sort(result);
        return result;
    }
}
