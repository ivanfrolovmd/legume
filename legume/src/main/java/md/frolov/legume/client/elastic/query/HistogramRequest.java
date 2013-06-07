package md.frolov.legume.client.elastic.query;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;
import md.frolov.legume.client.elastic.model.request.Facet;
import md.frolov.legume.client.elastic.model.request.QueryString;
import md.frolov.legume.client.elastic.model.request.RangeFacet;
import md.frolov.legume.client.elastic.model.request.SearchQuery;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramRequest implements Query
{
    private Date fromDate;
    private Date toDate;
    private long step;
    private int stepCount;
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

    /** @return resolution step in ms */
    public long getStep()
    {
        return step;
    }

    /** @param step resolution step in ms */
    public void setStep(final long step)
    {
        this.step = step;
    }

    /** @return number of iterations/ranges */
    public int getStepCount()
    {
        return stepCount;
    }

    /** @param stepCount number of iterations/ranges */
    public void setStepCount(final int stepCount)
    {
        this.stepCount = stepCount;
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
        Preconditions.checkNotNull(fromDate, "Date margin cannot be null");
        Preconditions.checkNotNull(toDate, "Date margin cannot be null");

        Preconditions.checkArgument(step>0 || stepCount > 0, "Step must be greater than zero");

        ElasticSearchRequest esRequest = ModelFactory.INSTANCE.elasticSearchRequest().as();

        RangeFacet facet = ModelFactory.INSTANCE.rangeFacet().as();
        facet.setRange(Collections.<String, List<RangeFacet.RangeDef>>singletonMap("@timestamp", getRanges()));

        esRequest.setQuery(getSearchQuery());

        esRequest.setFacets(Collections.<String, Facet>singletonMap("range", facet));
        esRequest.setSize(0);

        return esRequest;
    }

    private SearchQuery getSearchQuery() {
        if(query!=null && query.length()>0) {
            SearchQuery searchQuery = ModelFactory.INSTANCE.searchQuery().as();
            QueryString queryString = ModelFactory.INSTANCE.queryString().as();
            queryString.setQuery(query);
            searchQuery.setQueryString(queryString);
            return searchQuery;
        } else {
            return null;
        }
    }

    private List<RangeFacet.RangeDef> getRanges()
    {
        List<RangeFacet.RangeDef> ranges = Lists.newArrayList();

        if (toDate.compareTo(fromDate) < 0)
        {
            toDate = fromDate;
        }

        long daStep;
        if(step==0){
            daStep = (toDate.getTime() - fromDate.getTime())/stepCount;
        } else {
            daStep = step;
        }
        Preconditions.checkArgument(daStep>0, "Iteration step cannot be calculated");

        long current = fromDate.getTime();
        long to = toDate.getTime();
        while (current <= to)
        {
            RangeFacet.RangeDate range = ModelFactory.INSTANCE.rangeFacetRangeDate().as();
            range.setFrom(new Date(current));
            range.setTo(new Date(current + daStep));
            ranges.add(range);
            current += daStep;
        }
        return ranges;
    }
}
