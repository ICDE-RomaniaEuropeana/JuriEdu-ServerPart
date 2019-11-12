package org.icde.juriedu.search;

import org.icde.juriedu.model.EntryType;

import java.util.Objects;

public class InputObject {

    private EntryType type;
    private String search = "";
    private Integer size = 50;

    public EntryType getType() {
        return type;
    }

    public InputObject setType(EntryType type) {
        this.type = Objects.requireNonNull(type);
        return this;
    }

    public String getSearch() { return search; }

    public InputObject setSearch(String search) {
        Objects.requireNonNull(search);Objects.requireNonNull(search);
        return this;
    }

    public Integer getSize() { return size; }

    public InputObject setSize(Integer size) {
        this.size = Objects.requireNonNull(size);
        return this;
    }
}
