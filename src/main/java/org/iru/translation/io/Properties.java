package org.iru.translation.io;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.iru.translation.TranslationException;

public class Properties extends LinkedList<Property> {
    
    private final Map<String, Property> entries = new HashMap<>();

    void load(FileReader reader) throws TranslationException {
        try (LineNumberReader lr =new LineNumberReader(reader)) {
            String line;
            line = lr.readLine();
            while (line != null) {
                final Property property = parse(line, lr.getLineNumber());
                if (property.getType() == Property.Type.ENTRY) {
                    entries.put(property.getKey(), property);
                }
                add(property);
                line = lr.readLine();
            }
        } catch (IOException ex) {
            throw new TranslationException("Unable to read property file", ex);
        }
    }
    
    protected Property parse(String line, int lineNumber) throws TranslationException {
        String lineToParse = line.trim();
        if (lineToParse.isEmpty()) {
            return new Property(Property.Type.BLANK_LINE);
        } else if (lineToParse.startsWith("#")) {
            return new Property(Property.Type.COMMENT);
        } else {
            if (!line.contains("=")) {
                throw new TranslationException("Invalid property line: " + line);
            }
            String[] prop = line.split("=");
            return new Property(prop[0].trim(), prop[1].trim(), Property.Type.ENTRY);
        }
    }

    public void store(FileWriter fileWriter) throws TranslationException {
        Iterator<Property> i = iterator();
        while (i.hasNext()) {
            Property p = i.next();
            try {
                fileWriter.write(p.getKey() + "=" + p.getValue() + "\r\n");
            } catch (IOException ex) {
                throw new TranslationException("Unable to store properties", ex);
            }
        }
    }
    
    public void set(String key, String value) {
        Property p = entries.get(key);
        if (p != null) {
            p.setValue(value);
        } else {
            final Property property = new Property(key, value, Property.Type.ENTRY);
            entries.put(key, property);
            add(property);
        }
    }
    
    public Set<Map.Entry<String, Property>> entrySet() {
        return entries.entrySet();
    }

    public Property get(String key) {
        return entries.get(key);
    }

}
