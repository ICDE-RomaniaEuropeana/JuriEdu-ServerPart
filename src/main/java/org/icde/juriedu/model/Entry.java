package org.icde.juriedu.model;

public class Entry {
    private String key;
    private String message;
    private EntryType entryType;

    public String getKey() {
        return key;
    }

    public Entry setKey(String key) {
        this.key = key;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Entry setMessage(String message) {
        this.message = message;
        return this;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public Entry setEntryType(EntryType entryType) {
        this.entryType = entryType;
        return this;
    }
}
