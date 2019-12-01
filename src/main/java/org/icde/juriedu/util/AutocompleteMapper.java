package org.icde.juriedu.util;

import org.icde.juriedu.model.Question;
import org.icde.juriedu.model.autocompete.AutocompleteAnswer;
import org.icde.juriedu.model.autocompete.AutocompleteQuestion;
import org.icde.juriedu.model.autocompete.AutocompleteResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutocompleteMapper {
    public static AutocompleteResponse mapToAutocomplete(List<Question> questions) {
        List<AutocompleteQuestion> autocompleteQuestions = questions.stream()
                .flatMap(AutocompleteMapper::toAutocompleteQuestion)
                .collect(Collectors.toList());

        Map<String, AutocompleteAnswer> results = toResponses(questions);

        return new AutocompleteResponse(autocompleteQuestions, results);
    }

    private static Stream<AutocompleteQuestion> toAutocompleteQuestion(Question question) {
        return question.getQuestions().stream()
                .map(questionStr -> new AutocompleteQuestion(questionStr, question.getId()));
    }

    private static Map<String, AutocompleteAnswer> toResponses(List<Question> questions) {
        return questions.stream()
                .collect(Collectors.toMap(Question::getId, AutocompleteAnswer::fromQuestion));
    }
}
