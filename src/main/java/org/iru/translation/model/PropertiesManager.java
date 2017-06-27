package org.iru.translation.model;

import org.iru.translation.io.ConfigurationUpdater;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.io.ProvidedURLLocationStrategy;
import org.iru.translation.DictionnaryManager;
import org.iru.translation.TranslationException;
import org.iru.translation.gui.Action;
import org.iru.translation.io.EpdPropertiesConfigurationLayout;
import org.iru.translation.io.LineBreakIOFactory;

public class PropertiesManager {
    
    private final DictionnaryManager dictionnaryManager;
    
    public PropertiesManager(DictionnaryManager dictionnaryManager) {
        this.dictionnaryManager = dictionnaryManager;
    }
    
    public FileBasedConfigurationBuilder getPropertiesBuilder(File f) throws TranslationException {
        try {
            FileBasedConfigurationBuilder builder
                = new FileBasedConfigurationBuilder(PropertiesConfiguration.class)
                    .configure(new Parameters().properties()
                        .setLocationStrategy(new ProvidedURLLocationStrategy())
                        .setLayout(new EpdPropertiesConfigurationLayout())
                        .setEncoding("UTF-8")
                        .setIOFactory(new LineBreakIOFactory())
                        .setURL(f.toURI().toURL()));
            return builder;
        } catch (MalformedURLException ex) {
            throw new TranslationException("unable to get URL for file", ex);
        }
    }
    
    public List<Property> loadProperties(Configuration props) {
        return Stream.generate(props.getKeys()::next).limit(props.size())
            .filter(k -> !dictionnaryManager.isInDictionnary(props.getString(k)))
            .sorted(Comparator.naturalOrder())
            .map(k -> new Property(k, props.getString(k), null, Action.NONE))
            .collect(Collectors.toList());
    }

    public List<Property> diff(Configuration fromProps, Configuration toProps, ConfigurationUpdater updater) {
        List<Property> result = new LinkedList<>();
        Set<Object> insertedkeys = new HashSet<>(fromProps.size());
        Stream.generate(fromProps.getKeys()::next).limit(fromProps.size())
            .filter(k -> !dictionnaryManager.isInDictionnary(fromProps.getString(k)))
            .forEach(k -> {
                String toValueAsString = toProps.getString(k);
                final String fromValueAsString = fromProps.getString(k);
                if (toValueAsString == null) {
                    result.add(new Property(k, fromValueAsString, null, Action.DELETED, updater));
                } else if (fromValueAsString.trim().equalsIgnoreCase(toValueAsString.trim())) {
                    result.add(new Property(k, fromValueAsString, toValueAsString, Action.UNTRANSLATED, updater));
                } else if (!insertedkeys.contains(k)) {
                    insertedkeys.add(k);
                    result.add(new Property(k, fromValueAsString, toValueAsString, Action.NONE, updater));
                }
            });
        Stream.generate(toProps.getKeys()::next).limit(toProps.size())
            .filter(k -> !dictionnaryManager.isInDictionnary(toProps.getString(k)))
            .forEach(k -> {
                String fromValueAsString = fromProps.getString(k);
                if (fromValueAsString == null) {
                    result.add(new Property(k, null, toProps.getString(k), Action.ADDED, updater));
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
    
    public void importFromCsv(Configuration toProps, File f) throws TranslationException {
        final FileReader fileReader;
        try {
            fileReader = new FileReader(f);
        } catch (FileNotFoundException ex) {
            throw new TranslationException("File to import not found", ex);
        }
        LineNumberReader is = new LineNumberReader(fileReader);
        Map<String, String> props = new HashMap<>();
        try {
            String line = is.readLine();
            while (line != null) {
                String[] prop = line.split(";");
                if (prop.length != 2) {
                    throw new TranslationException("Format error at line " + is.getLineNumber());
                }
                String key = prop[0];
                String value = prop[1];
                props.put(key, value);
                line = is.readLine();
            }
        }
        catch (IOException ex) {
            throw new TranslationException("Error when reading CSV file", ex);
        }
        props.forEach((k, v) -> toProps.setProperty(k,v));
    }
}
