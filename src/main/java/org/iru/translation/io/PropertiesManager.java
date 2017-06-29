package org.iru.translation.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.iru.translation.DictionnaryManager;
import org.iru.translation.TranslationException;
import org.iru.translation.gui.Action;
import org.iru.translation.model.PropertyTableModel;
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
        return props.entrySet().stream()
            .filter(e -> (!dictionnaryManager.isKeyInDictionnary(e.getKey()) && !dictionnaryManager.isValueInDictionnary(e.getValue().getValue())))
            .sorted((e1, e2) -> {return e1.getKey().compareToIgnoreCase(e2.getKey());})
            .map(e -> new PropertyTableModel.Property(e.getKey(), e.getValue().getValue(), null, Action.NONE))
            .collect(Collectors.toList());
    }

    public List<Property> diff(Properties fromProps, Properties toProps) {
        List<Property> result = new LinkedList<>();
        Set<Object> insertedkeys = new HashSet<>(fromProps.size());
        fromProps.entrySet().stream()
            .filter(e -> (!dictionnaryManager.isKeyInDictionnary(e.getKey()) && !dictionnaryManager.isValueInDictionnary(e.getValue().getValue())))
            .forEach(e -> {
                String toValueAsString = toProps.get(e.getKey()).getValue();
                final String fromValueAsString = e.getValue().getValue();
                if (toValueAsString == null) {
                    result.add(new Property(e.getKey(), fromValueAsString, null, Action.DELETED));
                } else if (fromValueAsString.trim().equalsIgnoreCase(toValueAsString.trim())) {
                    result.add(new Property(e.getKey(), e.getValue().getValue(), toValueAsString, Action.UNTRANSLATED));
                } else if (!insertedkeys.contains(e.getKey())) {
                    insertedkeys.add(e.getKey());
                    result.add(new Property(e.getKey(), fromValueAsString, toValueAsString, Action.NONE));
                }
            });
        toProps.entrySet().stream()
            .filter(e -> (!dictionnaryManager.isKeyInDictionnary(e.getKey()) && !dictionnaryManager.isValueInDictionnary(e.getValue().getValue())))
            .forEach(p -> {
                String fromValueAsString = fromProps.get(p.getKey()).getValue();
                if (fromValueAsString == null) {
                    result.add(new Property(p.getKey(), null, p.getValue().getValue(), Action.ADDED));
                }
            });
        Collections.sort(result);
        return result;
    }
    
    public void export(PropertyTableModel tableModel) throws TranslationException {
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
            throw new TranslationException("Impossible to export data", ex);
        }
    }
}
