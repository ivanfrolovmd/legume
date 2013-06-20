package md.frolov.legume.client.elastic.api;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.model.query.AndFilter;
import md.frolov.legume.client.elastic.model.query.DateHistogramFacet;
import md.frolov.legume.client.elastic.model.query.ElasticSearchQuery;
import md.frolov.legume.client.elastic.model.query.Facet;
import md.frolov.legume.client.elastic.model.query.Filter;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramRequest extends BaseSearchRequest<HistogramResponse>
{
    private final Search search;
    private final HistogramInterval histogramInterval;
    private final int maximumSteps;

    public HistogramRequest(final Search search, int maximumSteps)
    {
        this.search = search;
        this.maximumSteps = maximumSteps;
        histogramInterval = calculateInterval();
    }

    public Search getSearch()
    {
        return search;
    }

    public HistogramInterval getHistogramInterval()
    {
        return histogramInterval;
    }

    private HistogramInterval calculateInterval() {
        if(histogramInterval!=null) {
            return histogramInterval;
        }

        long from = search.getRealFromDate();
        long to = search.getRealToDate();

        if(from == 0) {
            return HistogramInterval.h1;
        }

        long allTime = to - from;
        for (HistogramInterval histogramInterval : HistogramInterval.values())
        {
            if(allTime/histogramInterval.getTime()<maximumSteps) {
                return histogramInterval;
            }
        }
        return HistogramInterval.h1;
    }

    @Override
    public AutoBean<ElasticSearchQuery> getPayload()
    {
        Preconditions.checkNotNull(histogramInterval);

        ElasticSearchQuery esRequest = ModelFactory.INSTANCE.elasticSearchQuery().as();

        DateHistogramFacet facet = ModelFactory.INSTANCE.dateHistogramFacet().as();
        DateHistogramFacet.DateHistogramFacetDef def = ModelFactory.INSTANCE.dateHistogramFacetDef().as();
        facet.setDateHistogram(def);
        def.setField("@timestamp");
        def.setInterval(histogramInterval.getIntervalName());

        AndFilter and = ModelFactory.INSTANCE.andFilter().as();
        List<Filter> filters = Lists.newArrayList();
        filters.add(getQueryFilter(search.getQuery()));
        filters.add(getDateRangeFilter(search.getRealFromDate(), search.getRealToDate(), search.getToDate() == 0));
        and.setAnd(filters);

        facet.setFilter(and);

        esRequest.setFacets(Collections.<String, Facet>singletonMap("histo", facet));
        esRequest.setSize(0);

        return AutoBeanUtils.getAutoBean(esRequest);
    }

    @Override
    public HistogramResponse getResponse(final ElasticSearchReply elasticSearchReply)
    {
        return new HistogramResponse(elasticSearchReply, search.getRealFromDate(), search.getRealToDate(), histogramInterval);
    }
}
