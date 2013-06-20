package md.frolov.legume.client.elastic.api;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import md.frolov.legume.client.elastic.model.query.AndFilter;
import md.frolov.legume.client.elastic.model.query.ElasticSearchQuery;
import md.frolov.legume.client.elastic.model.query.Filter;
import md.frolov.legume.client.elastic.model.query.SortOrder;
import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchRequest extends BaseSearchRequest<SearchResponse>
{

    private final Search search;
    private boolean oldToNew;
    private long from;
    private int size;

    public SearchRequest(final Search search)
    {
        this(search, true, 0, 0);
    }

    public SearchRequest(final Search search, final boolean oldToNew, final long from, final int size)
    {
        this.search = search;
        this.oldToNew = oldToNew;
        this.from = from;
        this.size = size;
    }

    public long getFrom()
    {
        return from;
    }

    public void setFrom(final long from)
    {
        this.from = from;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(final int size)
    {
        this.size = size;
    }

    public Search getSearch()
    {
        return search;
    }

    public Object clone()
    {
        return new SearchRequest(search.clone(), oldToNew, from, size);
    }

    public void reverseOrder(){
        oldToNew = !oldToNew;
    }


    @Override
    public AutoBean<ElasticSearchQuery> getPayload()
    {
        ElasticSearchQuery esRequest = factory.elasticSearchQuery().as();
        esRequest.setFrom(from);
        esRequest.setSize(size);

        List<Filter> filters = Lists.newArrayList();
        filters.add(getQueryFilter(search.getQuery()));
        filters.add(getDateRangeFilter(search.getRealFromDate(), search.getRealToDate(), search.getToDate() == 0));
        AndFilter and = factory.andFilter().as();
        and.setAnd(filters);

        esRequest.setFilter(and);

        esRequest.setSort(Collections.singletonMap("@timestamp", oldToNew ? SortOrder.asc : SortOrder.desc));

        return AutoBeanUtils.getAutoBean(esRequest);
    }

    @Override
    public SearchResponse getResponse(final ElasticSearchReply elasticSearchReply)
    {
        return new SearchResponse(elasticSearchReply);
    }
}
