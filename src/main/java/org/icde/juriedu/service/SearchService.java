package org.icde.juriedu.service;

import org.apache.http.HttpHost;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.icde.juriedu.model.DictionaryTerm;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.util.JsonUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class SearchService {

    private RestHighLevelClient client;

    public SearchService() {}

    @Inject
    public SearchService(@ConfigProperty(name = "elasticsearch.host") String esHost,
                         @ConfigProperty(name = "elasticsearch.port") int esPort,
                         @ConfigProperty(name = "elasticsearch.scheme") String esScheme) {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort, esScheme)));
    }

    public List<Question> search(String searchKey, IndexType indexType, int size) {
        try {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightAnswer = new HighlightBuilder.Field("answer");
            highlightAnswer.highlighterType("unified");
            highlightAnswer.preTags("<em class='highlight'>");
            highlightAnswer.postTags("</em>");
            highlightAnswer.numOfFragments(0);
            highlightBuilder.field(highlightAnswer);

            String[] indexNames = (indexType != null ? Stream.of(indexType) : Stream.of(IndexType.values()))
                    .map(Enum::name)
                    .toArray(String[]::new);
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                    .query(createQuerySearch(searchKey))
                    .size(size)
                    .highlighter(highlightBuilder);
            SearchRequest searchRequest = new SearchRequest(indexNames)
                    .source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            return Arrays.stream(searchResponse.getHits().getHits())
                    .map(this::buildHighlightedQuestion)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Question> autocomplete(String searchKey, IndexType indexType, int size) {
        try {
            String[] indexNames = (indexType != null ? Stream.of(indexType) : Stream.of(IndexType.values()))
                    .map(Enum::name)
                    .toArray(String[]::new);
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                    .query(createQueryAutocomplete(searchKey))
                    .size(size);
            SearchRequest searchRequest = new SearchRequest(indexNames)
                    .source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return Arrays.stream(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(JsonUtil::fromJsonToQuestion)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<DictionaryTerm> getDictionaryEntry(String key) {
        try {
            GetRequest getRequest = new GetRequest(IndexType.dictionary.name())
                    .id(key);
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            return Optional.ofNullable(getResponse)
                    .map(GetResponse::getSourceAsString)
                    .map(JsonUtil::fromJsonToDictionaryEntry);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void save(DictionaryTerm dictionaryTerm) {
        save(dictionaryTerm, DictionaryTerm::getKey, IndexType.dictionary);
    }

    public void save(List<Question> questions) {
        for(Question question:questions) {
            save(question, Question::getId, IndexType.question);
        }
    }

    private <T> void  save(T object, Function<T, String> idMapper, IndexType indexType) {
        try {
            IndexRequest request = new IndexRequest(indexType.name())
                    .id(idMapper.apply(object))
                    .source(JsonUtil.toJson(object), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private QueryBuilder createQuerySearch(String searchKey) {
        if (searchKey != null) {
            String[] searchElements = searchKey.split(" ");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("questions", searchElements).boost(4f))
                    .should(QueryBuilders.termsQuery("answer", searchElements).boost(2f));
            for (String searchElement : searchElements) {
                queryBuilder = queryBuilder
                        .should(QueryBuilders.prefixQuery("questions", searchElement).boost(2f))
                        .should(QueryBuilders.prefixQuery("answer", searchElement));
            }

            return queryBuilder;
        }

        return QueryBuilders.matchAllQuery();
    }

    private QueryBuilder createQueryAutocomplete(String searchKey) {
        if (searchKey != null) {
            String[] searchElements = searchKey.split(" ");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("questions", searchElements));

            for (String searchElement : searchElements) {
                queryBuilder = queryBuilder
                        .should(QueryBuilders.prefixQuery("questions", searchElement));

            }
            return queryBuilder;
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }

    private Question buildHighlightedQuestion(SearchHit hit) {
        Question question = JsonUtil.fromJsonToQuestion(hit.getSourceAsString());

        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get("answer");

        if (highlight != null) {
            Text[] fragments = highlight.fragments();
            String fragmentString = fragments[0].string();
            question.setAnswer(fragmentString);
        }

        return question;
    }
}
