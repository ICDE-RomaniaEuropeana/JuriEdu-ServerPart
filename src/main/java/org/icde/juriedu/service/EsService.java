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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.icde.juriedu.model.Entry;
import org.icde.juriedu.model.EntryType;
import org.icde.juriedu.util.JsonUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class EsService {

    private final RestHighLevelClient client;

    @Inject
    public EsService(@ConfigProperty(name = "elasticsearch.host") String esHost,
                     @ConfigProperty(name = "elasticsearch.port") int esPort,
                     @ConfigProperty(name = "elasticsearch.scheme") String esScheme) {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort, esScheme)));
    }

    public List<Entry> search(String searchKey, EntryType entryType, int size) {
        try {
            String[] indexNames = (entryType != null ? Stream.of(entryType) : Stream.of(EntryType.values()))
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
                    .map(JsonUtil::fromJsonToEntry)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private QueryBuilder createQuery(String searchKey) {
        if (searchKey != null) {
            String[] searchElements = searchKey.split(" ");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("key", searchElements).boost(4f))
                    .should(QueryBuilders.termsQuery("message", searchElements).boost(2f));
            for (String searchElement : searchElements) {
                queryBuilder = queryBuilder
                        .should(QueryBuilders.prefixQuery("key", searchElement).boost(2f))
                        .should(QueryBuilders.prefixQuery("message", searchElement));
            }
            return queryBuilder;
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }

    public Optional<Entry> getDictionaryEntry(String key) {
        try {
            GetRequest getRequest = new GetRequest(EntryType.dictionary.name())
                    .id(key);
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            return Optional.ofNullable(getResponse)
                    .map(GetResponse::getSourceAsString)
                    .map(JsonUtil::fromJsonToEntry);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void save(Entry entry) {
        try {
            String indexName = entry.getEntryType().name();
            IndexRequest request = new IndexRequest(indexName)
                    .id(entry.getKey())
                    .source(JsonUtil.toJson(entry), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
