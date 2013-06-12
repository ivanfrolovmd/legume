package md.frolov.legume.client.elastic.api;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gwt.i18n.client.TimeZone;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.model.query.*;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.util.ConversionUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsFacetRequest extends ESRequest<ElasticSearchQuery, ElasticSearchReply, TermsFacetResponse>
{
    private static final int MAX_TERM_SIZE = 30;
    private static final long MAX_ITEM_COUNT_PER_SHARD = 2000;

    private TermsFacetRequest() {}
    private String fieldName;
    private String query;
    private Date fromDate;
    private Date toDate;

    public static TermsFacetRequest create() {
        return new TermsFacetRequest();
    }

    public TermsFacetRequest withField(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public TermsFacetRequest withQueryFilter(String queryFilter) {
        query = queryFilter;
        return  this;
    }

    public TermsFacetRequest withDatesFilter(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        return this;
    }

    @Override
    public String getUri()
    {
        return "/_search";
    }

    @Override
    public AutoBean<ElasticSearchQuery> getPayload()
    {
        ElasticSearchQuery esQuery = factory.elasticSearchQuery().as();

        TermFacet facet = factory.termFacet().as();
        TermFacet.TermsDef facetDef = factory.termFacetDef().as();
        facet.setTerms(facetDef);

        facetDef.setScriptField(getFieldForScriptName());
        facetDef.setSize(MAX_TERM_SIZE);

        AndFilter and = ModelFactory.INSTANCE.andFilter().as();
        List<Filter> filters = Lists.newArrayList();
        appendQueryFilter(filters);
        appendDateRangeFilter(filters);
        appendLimitFilter(filters);
        and.setAnd(filters);
        facet.setFilter(and);

        esQuery.setFacets(Collections.<String, Facet>singletonMap("term", facet));
        esQuery.setSize(0);

        return AutoBeanUtils.getAutoBean(esQuery);
    }

    private String getFieldForScriptName() {
        StringBuilder sb = new StringBuilder();
        sb.append("_source");
        Iterable<String> parts = Splitter.on('.').split(fieldName);
        for (String part : parts)
        {
            sb.append("[\"").append(part).append("\"]");
        }
        return sb.toString();
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

    private void appendLimitFilter(List<Filter> filters) {
        //TODO check how this influences results
        LimitFilter filter = factory.limitFilter().as();
        LimitFilter.LimitFilterDef filterDef = factory.limitFilterDef().as();
        filter.setLimit(filterDef);
        filterDef.setValue(MAX_ITEM_COUNT_PER_SHARD);
        filters.add(filter);
    }

    @Override
    public AutoBean<ElasticSearchReply> getExpectedAutobeanResponse()
    {
        return factory.elasticSearchReply();
    }

    @Override
    public TermsFacetResponse getResponse(final ElasticSearchReply elasticSearchReply)
    {
        return new TermsFacetResponse(elasticSearchReply);
    }

    public static TermsFacetRequest fromHistoryToken(String token)
    {
        //TODO deal with exceptions
        Iterable<String> parts = Splitter.on('/').limit(4).split(token);
        Iterator<String> it = parts.iterator();
        
        Long fromDateTime = Long.valueOf(it.next());
        Date fromDate;
        if(fromDateTime == 0) {
            fromDate = null;
        } else {
            fromDate = new Date(fromDateTime);
        }

        Long toDateTime = Long.valueOf(it.next());
        Date toDate;
        if(toDateTime == 0) {
            toDate = null;
        } else {
            toDate = new Date(toDateTime);
        }

        String field = it.next();
        String query = it.next();

        return create().withDatesFilter(fromDate,toDate).withField(field).withQueryFilter(query);
    }

    public String toHistoryToken()
    {
        return Joiner.on('/').join(new Object[]{
                fromDate == null ? 0: fromDate.getTime(),
                toDate == null ? 0: toDate.getTime(),
                fieldName,
                query
        });
    }
}
