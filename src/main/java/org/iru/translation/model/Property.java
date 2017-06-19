package org.iru.translation.model;

import org.iru.translation.gui.Action;

public class Property implements Comparable<Property> {
    private final String key;
    private final String valueFrom;
    private final String valueTo;
    private final Action action;

    public Property(String key, String valueFrom, String valueTo, Action action) {
        this.key = key;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.action = action;
    }

    public String getKey() {
        return key;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    public String getValueTo() {
        return valueTo;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public int compareTo(Property o) {
        return key.compareToIgnoreCase(o.key);
    }

}
