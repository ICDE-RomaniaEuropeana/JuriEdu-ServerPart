package org.icde.juriedu.search;

import org.icde.juriedu.model.EntryType;

public class InputObject {

    private EntryType type;
    private String search;
    private Integer size = 50;

    public EntryType getType() {
        return type;
    }

    public InputObject setType(EntryType type) {
        this.type = type;
        return this;
    }

    public String getSearch() { return search; }

    public InputObject setSearch(String search) {
        this.search = search;
        return this;
    }

    public Integer getSize() { return size; }

    public InputObject setSize(Integer size) {
        this.size = size;
        return this;
    }
}
