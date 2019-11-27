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
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.icde.juriedu.model.DictionaryEntry;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.util.JsonUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
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
            String[] indexNames = (indexType != null ? Stream.of(indexType) : Stream.of(IndexType.values()))
                    .map(Enum::name)
                    .toArray(String[]::new);
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                    .query(createQuery(searchKey))
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

    public List<Question> searchQuestion(String searchKey, IndexType indexType, int size) {
        try {
            String[] indexNames = (indexType != null ? Stream.of(indexType) : Stream.of(IndexType.values()))
                    .map(Enum::name)
                    .toArray(String[]::new);
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                    .query(createQueryQuestion(searchKey))
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

    public Optional<DictionaryEntry> getDictionaryEntry(String key) {
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

    public void save(DictionaryEntry dictionaryEntry) {
        save(dictionaryEntry, DictionaryEntry::getKey, IndexType.dictionary);
    }

    public void save(List<Question> questions) {
        for(Question question:questions) {
            save(question, Question::getQuestion, IndexType.question);
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

    private QueryBuilder createQuery(String searchKey) {
        if (searchKey != null) {
            String[] searchElements = searchKey.split(" ");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("question", searchElements).boost(4f))
                    .should(QueryBuilders.termsQuery("answer", searchElements).boost(2f));
            for (String searchElement : searchElements) {
                queryBuilder = queryBuilder
                        .should(QueryBuilders.prefixQuery("question", searchElement).boost(2f))
                        .should(QueryBuilders.prefixQuery("answer", searchElement));
            }
            return queryBuilder;
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }


    private QueryBuilder createQueryQuestion(String searchKey) {
        if (searchKey != null) {
            String[] searchElements = searchKey.split(" ");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("question", searchElements))
                    .should(QueryBuilders.termsQuery("related", searchElements));

            for (String searchElement : searchElements) {
                queryBuilder = queryBuilder
                        .should(QueryBuilders.prefixQuery("question", searchElement))
                        .should(QueryBuilders.prefixQuery("related", searchElement));

            }
            return queryBuilder;
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }
}
