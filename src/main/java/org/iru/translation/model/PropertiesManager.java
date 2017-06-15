package org.iru.translation.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.iru.translation.DictionnaryManager;
import org.iru.translation.PreferencesException;
import org.iru.translation.TranslationException;
import org.iru.translation.gui.Action;
import org.iru.translation.model.PropertyTableModel.Property;

public class PropertiesManager {
    
    private final DictionnaryManager dictionnaryManager;
    
    public PropertiesManager(DictionnaryManager dictionnaryManager) {
        this.dictionnaryManager = dictionnaryManager;
    }
    
    public Properties readProperties(File f) throws TranslationException {
        Properties p = new Properties();
        try(FileReader reader = new FileReader(f)) {
            p.load(reader);
        } catch (IOException ex) {
            throw new TranslationException("Unable to parse file", ex);
        }
        return p;
    }
    
    public List<Property> loadProperties(Properties props) {
        return props.entrySet()
            .stream()
            .filter(p -> !dictionnaryManager.isInDictionnary((String) p.getValue()))
            .sorted((p1,p2) -> {return p1.getKey().toString().compareToIgnoreCase(p2.getKey().toString());})
            .map(p -> new PropertyTableModel.Property((String) p.getKey(), (String) p.getValue(), null, Action.NONE))
            .collect(Collectors.toList());
    }

    public List<Property> diff(Properties fromProps, Properties toProps) {
        List<Property> result = new LinkedList<>();
        Set<Object> insertedkeys = new HashSet<>(fromProps.size());
        fromProps.entrySet().stream()
            .filter(p -> !dictionnaryManager.isInDictionnary((String) p.getValue()))
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
            .filter(p -> !dictionnaryManager.isInDictionnary((String) p.getValue()))
            .forEach(p -> {
                String fromValueAsString = (String)fromProps.get(p.getKey());
                if (fromValueAsString == null) {
                    result.add(new Property((String)p.getKey(), null, (String)p.getValue(), Action.ADDED));
                }
            });
        Collections.sort(result);
        return result;
    }
    
    public void export(PropertyTableModel tableModel) throws PreferencesException {
        String newLine = System.getProperty("line.separator");
        File f = new File(System.getProperty("java.io.tmpdir") + "/translations-export.csv"); 
        try (FileWriter fw = new FileWriter(f)) {
            for (int i=0; i<tableModel.getRowCount(); i++) {
                final Property prop = tableModel.getModel(i);
                fw.append(prop.getKey()).append(';');
                if (prop.getValueFrom() != null) {
                    fw.append(prop.getValueFrom().replace("\n", "<LINEBREAK>")).append(';');
                }
                if (prop.getValueTo() != null) {
                    fw.append(prop.getValueTo().replace("\n", "<LINEBREAK>")).append(';');
                }
                fw.append(newLine);
            }
        } catch(Exception ex) {
            throw new PreferencesException("Impossible to export data", ex);
        }
    }
}
