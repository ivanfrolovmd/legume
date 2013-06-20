package md.frolov.legume.client.elastic.api;

import java.util.Collections;
import java.util.Date;

import com.google.gwt.i18n.client.TimeZone;
import com.google.web.bindery.autobean.shared.AutoBean;

import md.frolov.legume.client.elastic.model.query.ElasticSearchQuery;
import md.frolov.legume.client.elastic.model.query.Filter;
import md.frolov.legume.client.elastic.model.query.QueryFilter;
import md.frolov.legume.client.elastic.model.query.QueryString;
import md.frolov.legume.client.elastic.model.query.RangeFilter;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.util.ConversionUtils;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public abstract class BaseSearchRequest<RESPONSE extends ESResponse<ElasticSearchReply>> extends ESRequest<ElasticSearchQuery, ElasticSearchReply, RESPONSE>
{
    @Override
    public String getUri()
    {
        return "/_search";
    }

    @Override
    public AutoBean<ElasticSearchReply> getExpectedAutobeanResponse()
    {
        return factory.elasticSearchReply();
    }

    protected Filter getQueryFilter(String searchQueryString) {
        if(searchQueryString!=null && searchQueryString.length()>0) {
            QueryFilter queryFilter = factory.queryFilter().as();

            QueryString queryString = factory.queryString().as();
            QueryString.QueryStringDef queryStringDef = factory.queryStringDef().as();
            queryString.setQueryString(queryStringDef);

            queryStringDef.setQuery(searchQueryString);
            queryFilter.setQuery(queryString);
            return queryFilter;
        } else {
            //TODO return match_all?
            return null;
        }
    }

    protected Filter getDateRangeFilter(long from, long to, boolean upperUnbound) {
        RangeFilter filter = factory.rangeFilter().as();
        RangeFilter.RangeFilterDef filterDef = factory.rangeFilterDef().as();
        filter.setRange(Collections.singletonMap("@timestamp", filterDef));
        filterDef.setFrom(ConversionUtils.INSTANCE.dateToString(new Date(from), TimeZone.createTimeZone(0)));
        if(!upperUnbound) {
            filterDef.setTo(ConversionUtils.INSTANCE.dateToString(new Date(to), TimeZone.createTimeZone(0)));
        } //else leave unbounded

        return filter;
    }
}
