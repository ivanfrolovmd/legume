package md.frolov.legume.client.elastic.api;

import java.util.List;

import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.model.reply.SearchHit;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchResponse extends ESResponse<ElasticSearchReply>
{
    private final List<SearchHit> hits;

    public SearchResponse(final ElasticSearchReply elasticSearchReply)
    {
        hits = elasticSearchReply.getHits().getHits();
    }

    public List<SearchHit> getHits()
    {
        return hits;
    }
}
