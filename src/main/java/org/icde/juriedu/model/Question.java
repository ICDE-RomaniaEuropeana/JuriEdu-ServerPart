package org.icde.juriedu.model;

import java.util.List;

public class Question {
    private String chapter;
    private String question;
    private String answer;
    private List<String> related;
    private ColorCode colorCode = ColorCode.black;

    public String getChapter() {
        return chapter;
    }

    public Question setChapter(String chapter) {
        this.chapter = chapter;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public Question setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public Question setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public List<String> getRelated() {
        return related;
    }

    public Question setRelated(List<String> related) {
        this.related = related;
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
