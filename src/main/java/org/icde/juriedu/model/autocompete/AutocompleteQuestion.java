package org.icde.juriedu.model.autocompete;

public class AutocompleteQuestion {
    private String label;
    private String id;

    public AutocompleteQuestion(String label, String id) {
        this.label = label;
        this.id = id;
    }

    public AutocompleteQuestion() {
    }

    public String getLabel() {
        return label;
    }

    public AutocompleteQuestion setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getId() {
        return id;
    }

    public AutocompleteQuestion setId(String id) {
        this.id = id;
        return this;
    }
}
