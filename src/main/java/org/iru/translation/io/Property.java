package org.iru.translation.io;

public class Property {
    
    public static enum Type {
        COMMENT,
        BLANK_LINE,
        ENTRY,
    }
    
    private String key = null;
    private String value = null;
    private final Type type;

    public Property(String key, String value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public Property(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
