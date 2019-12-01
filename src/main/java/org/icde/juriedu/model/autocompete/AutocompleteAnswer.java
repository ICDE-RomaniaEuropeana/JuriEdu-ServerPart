package org.icde.juriedu.model.autocompete;

import org.icde.juriedu.model.Question;

public class AutocompleteAnswer {
    private String chapter;
    private String answer;

    public static AutocompleteAnswer fromQuestion(Question question) {
        return new AutocompleteAnswer()
                .setAnswer(question.getAnswer())
                .setChapter(question.getChapter());
    }

    public String getChapter() {
        return chapter;
    }

    public AutocompleteAnswer setChapter(String chapter) {
        this.chapter = chapter;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public AutocompleteAnswer setAnswer(String answer) {
        this.answer = answer;
        return this;
    }
}
