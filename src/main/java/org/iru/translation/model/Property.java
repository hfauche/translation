package org.iru.translation.model;

import org.iru.translation.io.PropertyChangeListener;
import org.iru.translation.gui.Action;
import org.iru.translation.io.PropertyChangeListenerAble;

public class Property implements Comparable<Property>, PropertyChangeListenerAble {
    private final String key;
    private final String valueFrom;
    private String valueTo;
    private Action action;
    private PropertyChangeListener listener;

    public Property(String key, String valueFrom, String valueTo, Action action, PropertyChangeListener listener) {
        this.key = key;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.action = action;
        this.listener = listener;
    }
    
    public Property(String key, String valueFrom, String valueTo, Action action) {
        this.key = key;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.action = action;
    }
    
    @Override
    public String getKey() {
        return key;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    @Override
    public String getValueTo() {
        return valueTo;
    }

    public Action getAction() {
        return action;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }
    
    void setValueTo(String value) {
        this.valueTo = value;
        if (listener != null) {
            listener.notifyPropertyValueCĥange(this);
        }
    }

    @Override
    public int compareTo(Property o) {
        return key.compareToIgnoreCase(o.key);
    }

    @Override
    public String toString() {
        return "Property{" + "key=" + key + ", valueFrom=" + valueFrom + ", valueTo=" + valueTo + '}';
    }

}
