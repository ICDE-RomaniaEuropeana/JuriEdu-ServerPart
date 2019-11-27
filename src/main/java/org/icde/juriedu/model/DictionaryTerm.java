package org.icde.juriedu.model;

public class DictionaryTerm {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public DictionaryTerm setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public DictionaryTerm setValue(String value) {
        this.value = value;
        return this;
    }
}
