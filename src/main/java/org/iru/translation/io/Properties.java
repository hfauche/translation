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
    private final static String DOS_CRLF = "\r\n";

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
            return new Property(Property.Type.BLANK_LINE, lineNumber-1);
        } else if (lineToParse.startsWith("#")) {
            return new Property(line, Property.Type.COMMENT, lineNumber-1);
        } else {
            if (!line.contains("=")) {
                throw new TranslationException("Invalid property line: " + line);
            }
            String[] prop = line.split("=");
            return new Property(prop[0].trim(), prop[1].trim(), Property.Type.ENTRY, lineNumber-1);
        }
    }

    public void store(FileWriter fileWriter) throws TranslationException {
        Iterator<Property> i = iterator();
        while (i.hasNext()) {
            Property p = i.next();
            try {
                switch(p.getType()) {
                    case ENTRY:
                        fileWriter.write(p.getKey() + "=" + p.getValue());
                        break;
                    case COMMENT:
                        fileWriter.write(p.getValue());
                        break;
                    case BLANK_LINE:
                        break;
                }
                fileWriter.write(DOS_CRLF);
            } catch (IOException ex) {
                throw new TranslationException("Unable to store properties", ex);
            }
        }
    }
    
    public void set(String key, String value, int pos) {
        Property p = entries.get(key);
        if (p != null) {
            p.setValue(value);
        } else {
            final Property property = new Property(key, value, Property.Type.ENTRY, pos);
            entries.put(key, property);
            add(pos, property);
        }
    }
    
    public void set(String key, String value) {
        set(key, value, size());
    }
    
    public Set<Map.Entry<String, Property>> entrySet() {
        return entries.entrySet();
    }

    public Property get(String key) {
        return entries.get(key);
    }

}
