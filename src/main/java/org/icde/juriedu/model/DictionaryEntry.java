package org.icde.juriedu.model;

public class DictionaryEntry {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public DictionaryEntry setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public DictionaryEntry setValue(String value) {
        this.value = value;
        return this;
    }
}
