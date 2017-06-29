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
    private final int pos;

    public Property(String key, String value, Type type, int pos) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.pos = pos;
    }

    public Property(Type type, int pos) {
        this.type = type;
        this.pos = pos;
    }

    public Property(String value, Type type, int pos) {
        this.value = value;
        this.type = type;
        this.pos = pos;
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
    
    public int getPos() {
        return pos;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
