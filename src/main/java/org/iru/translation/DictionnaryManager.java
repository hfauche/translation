package org.iru.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DictionnaryManager {
    private Set<String> dicValues;
    private Set<String> dicKeys;
    private final String dicValuesFileLocation = System.getProperty("user.home") + "/translations-dictionnary-values.conf";
    private final String dicKeysFileLocation = System.getProperty("user.home") + "/translations-dictionnary-keys.conf";
    
    public void loadDictionnary() {
        File fv = new File(dicValuesFileLocation);
        if (fv.exists()) {
            try {
                dicValues = new HashSet<>();
                BufferedReader br = new BufferedReader(new FileReader(fv));
                String entry = br.readLine();
                do {
                    dicValues.add(entry);
                    entry = br.readLine();
                }
                while (entry != null);
            } catch (IOException ex) {}
        } else {
            dicValues = Collections.EMPTY_SET;
        }
        File fk = new File(dicKeysFileLocation);
        if (fk.exists()) {
            try {
                dicKeys = new HashSet<>();
                BufferedReader br = new BufferedReader(new FileReader(fk));
                String entry = br.readLine();
                do {
                    dicKeys.add(entry);
                    entry = br.readLine();
                }
                while (entry != null);
            } catch (IOException ex) {}
        } else {
            dicKeys = Collections.EMPTY_SET;
        }
    }
    
    public boolean isValueInDictionnary(String entry) {
        return dicValues.contains(entry);
    }
    
    public boolean isKeyInDictionnary(String entry) {
        return dicKeys.contains(entry);
    }
    
}
