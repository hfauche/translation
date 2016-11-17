package org.iru.translation.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public class PropertyModel extends DefaultListModel<PropertyModel.Property> {
    
    private static String sb = "....................................................................................................";
    private List<Property> propList = new ArrayList<>();

    public void setValues(List<Property> values) {
        propList = values;
        this.fireContentsChanged(this, 0, propList.size()-1);
    }
    
    @Override
    public void clear() {
        propList.clear();
    }
    
    @Override
    public int getSize() {
        return propList.size();
    }

    @Override
    public Property getElementAt(int index) {
        return propList.get(index);
    }

    public static class Property implements Comparable<Property>{
        
        private final String key;
        private final String value;
        private final Action action;

        public Property(String key, String value, Action action) {
            this.key = key;
            this.value = value;
            this.action = action;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public Action getAction() {
            return action;
        }
        
        @Override
        public String toString() {
            return new StringBuilder(key).append(sb, key.length(), 100).append(value).toString();
        }

        @Override
        public int compareTo(Property o) {
            return key.compareToIgnoreCase(o.key);
        }
        
    }

}
