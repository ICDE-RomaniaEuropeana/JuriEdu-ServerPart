package org.icde.juriedu.model.autocompete;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AutocompleteResponse {
    private List<AutocompleteQuestion> questions = Collections.emptyList();
    private Map<String, AutocompleteAnswer> results = Collections.emptyMap();

    public AutocompleteResponse() {
    }

    public AutocompleteResponse(List<AutocompleteQuestion> questions, Map<String, AutocompleteAnswer> results) {
        this.questions = questions;
        this.results = results;
    }

    public List<AutocompleteQuestion> getQuestions() {
        return questions;
    }

    public AutocompleteResponse setQuestions(List<AutocompleteQuestion> questions) {
        this.questions = questions;
        return this;
    }

    public Map<String, AutocompleteAnswer> getResults() {
        return results;
    }

    public AutocompleteResponse setResults(Map<String, AutocompleteAnswer> results) {
        this.results = results;
        return this;
    }
}
