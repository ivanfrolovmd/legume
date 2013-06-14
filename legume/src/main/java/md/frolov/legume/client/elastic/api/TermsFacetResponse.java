package md.frolov.legume.client.elastic.api;

import java.util.Map;

import com.google.common.collect.Maps;

import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.model.reply.Facet;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsFacetResponse extends ESResponse<ElasticSearchReply>
{
    private final Map<String, Long> terms = Maps.newLinkedHashMap();
    private final long total;
    private final long missing;
    private final long other;

    public TermsFacetResponse(final ElasticSearchReply elasticSearchReply)
    {
        Facet facet = elasticSearchReply.getFacets().values().iterator().next();

        for (Facet.Term term : facet.getTerms())
        {
            terms.put(term.getTerm(), term.getCount());
        }

        total = facet.getTotal();
        missing = facet.getMissing();
        other = facet.getOther();
    }

    public Map<String, Long> getTerms()
    {
        return terms;
    }

    public long getTotal()
    {
        return total;
    }

    public long getMissing()
    {
        return missing;
    }

    public long getOther()
    {
        return other;
    }

}
