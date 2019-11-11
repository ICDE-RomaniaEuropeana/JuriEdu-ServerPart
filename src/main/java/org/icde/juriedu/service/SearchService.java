package org.icde.juriedu.service;

import org.apache.http.HttpHost;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.icde.juriedu.search.OutputObject;
import org.icde.juriedu.model.Entry;
import org.icde.juriedu.model.EntryType;
import org.icde.juriedu.util.JsonUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class SearchService {

    private final RestHighLevelClient client;

    @Inject
    public SearchService(@ConfigProperty(name = "elasticsearch.host") String esHost,
                         @ConfigProperty(name = "elasticsearch.port") int esPort,
                         @ConfigProperty(name = "elasticsearch.scheme") String esScheme) {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort, esScheme)));
    }

    public OutputObject search(String searchKey, EntryType entryType, int size) {
        try {
            String[] indexNames = (entryType != null ? Stream.of(entryType) : Stream.of(entryType.values()))
                    .map(Enum::name)
                    .toArray(String[]::new);
            SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                    .query(createQuery(searchKey))
                    .size(size);
            SearchRequest searchRequest = new SearchRequest(indexNames)
                    .source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<Entry> result = Arrays.stream(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(JsonUtil::fromJsonToEntry)
                    .collect(Collectors.toList());

            OutputObject out = new OutputObject();
            out.setResult(result);
            return out;
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
}
