package md.frolov.legume.client.elastic.query;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.TimeZone;

import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.model.request.*;
import md.frolov.legume.client.util.ConversionUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramRequest implements RequestQuery
{

    private Date fromDate;
    private Date toDate;

    //step length in ms
    private HistogramInterval interval;

    private String query;

    public Date getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(final Date fromDate)
    {
        this.fromDate = fromDate;
    }

    public Date getToDate()
    {
        return toDate;
    }

    public void setToDate(final Date toDate)
    {
        this.toDate = toDate;
    }

    public void setInterval(final HistogramInterval interval)
    {
        this.interval = interval;
    }

    public HistogramInterval getInterval()
    {
        return interval;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(final String query)
    {
        this.query = query;
    }

    @Override
    public String getUri()
    {
        return "/_search";
    }

    @Override
    public ElasticSearchRequest getPayload()
    {
        Preconditions.checkNotNull(interval);

        ElasticSearchRequest esRequest = ModelFactory.INSTANCE.elasticSearchRequest().as();

        DateHistogramFacet facet = ModelFactory.INSTANCE.dateHistogramFacet().as();
        DateHistogramFacet.DateHistogramFacetDef def = ModelFactory.INSTANCE.dateHistogramFacetDef().as();
        facet.setDateHistogram(def);
        def.setField("@timestamp");
        def.setInterval(getIntervalStr());

        AndFilter and = ModelFactory.INSTANCE.andFilter().as();
        List<Filter> filters = Lists.newArrayList();
        appendQueryFilter(filters);
        appendDateRangeFilter(filters);
        and.setAnd(filters);

        facet.setFilter(and);

        esRequest.setFacets(Collections.<String, Facet>singletonMap("histo", facet));
        esRequest.setSize(0);

        return esRequest;
    }

    private void appendQueryFilter(List<Filter> filters){
        if(query!=null && query.length()>0) {
            QueryFilter queryFilter = ModelFactory.INSTANCE.queryFilter().as();

            QueryString queryString = ModelFactory.INSTANCE.queryString().as();
            QueryString.QueryStringDef queryStringDef = ModelFactory.INSTANCE.queryStringDef().as();
            queryString.setQueryString(queryStringDef);

            queryStringDef.setQuery(query);
            queryFilter.setQuery(queryString);
            filters.add(queryFilter);
        }
    }

    private void appendDateRangeFilter(List<Filter> filters)
    {
        RangeFilter filter = ModelFactory.INSTANCE.rangeFilter().as();
        RangeFilter.RangeFilterDef filterDef = ModelFactory.INSTANCE.rangeFilterDef().as();
        filter.setRange(Collections.singletonMap("@timestamp", filterDef));
        if(fromDate!=null) {
            filterDef.setFrom(ConversionUtils.INSTANCE.dateToString(fromDate, TimeZone.createTimeZone(0)));
        }
        if(toDate!=null) {
            filterDef.setTo(ConversionUtils.INSTANCE.dateToString(toDate, TimeZone.createTimeZone(0)));
        }
        filters.add(filter);
    }

    private String getIntervalStr() {
        return interval.getIntervalName();
    }

    @Override
    public RequestBuilder.Method getMethod()
    {
        return RequestBuilder.POST;
    }
}
