package md.frolov.legume.client.elastic.api;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.elastic.model.query.AndFilter;
import md.frolov.legume.client.elastic.model.query.ElasticSearchQuery;
import md.frolov.legume.client.elastic.model.query.Facet;
import md.frolov.legume.client.elastic.model.query.Filter;
import md.frolov.legume.client.elastic.model.query.LimitFilter;
import md.frolov.legume.client.elastic.model.query.TermFacet;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class TermsFacetRequest extends BaseSearchRequest<TermsFacetResponse>
{
    private static final int MAX_TERM_SIZE = 30;
    private static final long MAX_ITEM_COUNT_PER_SHARD = 2000;

    private final String fieldName;
    private final Search search;

    public TermsFacetRequest(final String fieldName, final Search search)
    {
        this.fieldName = fieldName;
        this.search = search;
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

        AndFilter and = factory.andFilter().as();
        List<Filter> filters = Lists.newArrayList();
        filters.add(getQueryFilter(search.getQuery()));
        filters.add(getDateRangeFilter(search.getFromDate(), search.getToDate()));
        filters.add(getLimitFilter());
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

    private Filter getLimitFilter() {
        //TODO check how this influences results
        LimitFilter filter = factory.limitFilter().as();
        LimitFilter.LimitFilterDef filterDef = factory.limitFilterDef().as();
        filter.setLimit(filterDef);
        filterDef.setValue(MAX_ITEM_COUNT_PER_SHARD);
        return filter;
    }


    @Override
    public TermsFacetResponse getResponse(final ElasticSearchReply elasticSearchReply)
    {
        return new TermsFacetResponse(elasticSearchReply);
    }


    public String getFieldName()
    {
        return fieldName;
    }
}
