package md.frolov.legume.client.elastic.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.model.reply.Facet;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramResponse extends ESResponse<ElasticSearchReply>
{
    private final Map<Long, Long> dateValueMap;
    private final HistogramInterval interval;

    public HistogramResponse(final ElasticSearchReply elasticSearchReply, long fromDate, long toDate, HistogramInterval interval)
    {
        this.interval = interval;
        Facet facet = elasticSearchReply.getFacets().values().iterator().next();
        List<Facet.Entry> entries = facet.getEntries();
        dateValueMap = Maps.newTreeMap();

        populateWithNulls(facet.getEntries(), fromDate, toDate, interval.getTime());
    }

    public Map<Long, Long> getDateValueMap()
    {
        return dateValueMap;
    }

    public HistogramInterval getInterval()
    {
        return interval;
    }

    private void populateWithNulls(final List<Facet.Entry> entries, long from, long to, final long interval)
    {
        if(entries==null || entries.isEmpty()) {
            dateValueMap.put(from, 0l);
            dateValueMap.put(to, 0l);
            return;
        }

        if(to==0) {
            to = new Date().getTime();
        }
        if(to<0) {
            to = new Date().getTime() + to;
        }

        if(from==0) {
            from = entries.get(0).getTime().getTime();
        }
        if(from<0) {
            from = to + from;
        }

        for(long t = entries.get(0).getTime().getTime(); t>from; t-=interval){
            dateValueMap.put(t, 0l);
        }
        for(long t=entries.get(0).getTime().getTime(); t<to; t+=interval) {
            dateValueMap.put(t, 0l);
        }
        for (Facet.Entry entry : entries)
        {
            dateValueMap.put(entry.getTime().getTime(), entry.getCount());
        }
    }
}
