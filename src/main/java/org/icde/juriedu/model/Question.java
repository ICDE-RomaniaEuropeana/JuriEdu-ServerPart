package org.icde.juriedu.model;

import java.util.List;
import java.util.UUID;

public class Question {
    private UUID id;
    private String chapter;
    private List<String> questions;
    private String answer;
    private ColorCode colorCode = ColorCode.black;

    public String getChapter() {
        return chapter;
    }

    public Question setChapter(String chapter) {
        this.chapter = chapter;
        return this;
    }

    public String getId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        return id.toString();
    }

    public Question setId(String id) {
        this.id = UUID.fromString(id);
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public Question setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public Question setRelated(List<String> questions) {
        this.questions = questions;
        return this;
    }

    public ColorCode getColorCode() {
        return colorCode;
    }

    public Question setColorCode(ColorCode colorCode) {
        this.colorCode = colorCode;
        return this;
    }
}
