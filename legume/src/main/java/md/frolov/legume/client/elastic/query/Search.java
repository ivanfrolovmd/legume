package md.frolov.legume.client.elastic.query;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.TimeZone;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.elastic.model.ModelFactory;
import md.frolov.legume.client.elastic.model.request.ElasticSearchRequest;
import md.frolov.legume.client.elastic.model.request.FilteredQuery;
import md.frolov.legume.client.elastic.model.request.QueryString;
import md.frolov.legume.client.elastic.model.request.SortOrder;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ConversionUtils;

public class Search implements RequestQuery
{
    public static final Search DEFAULT = new Search("", null, new Date(), null);
    private static final int DEFAULT_QUERY_SIZE = WidgetInjector.INSTANCE.configurationService().getInt(Constants.PAGE_SIZE);

    private String query;
    private Long from = 0l;
    private Integer size = DEFAULT_QUERY_SIZE;
    private Date fromDate;
    private Date toDate;
    private Date focusDate;
    private boolean sortByTimestampAsc = true;
    private boolean fetchFacets = true;

    /** Default constructor */
    public Search()
    {

    }

    public Search(final String query, final Date fromDate, final Date toDate, final Date focusDate)
    {
        this.query = query;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.focusDate = focusDate;
    }

    public void setQuery(final String query)
    {
        this.query = query;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(final int size)
    {
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

    public Date getFocusDate()
    {
        return focusDate;
    }

    public void setFocusDate(final Date focusDate)
    {
        this.focusDate = focusDate;
    }

    @Override
    public String getUri()
    {
        return "/_search";
    }

    @Override
    public ElasticSearchRequest getPayload()
    {
        ElasticSearchRequest esRequest = ModelFactory.INSTANCE.elasticSearchRequest().as();
        esRequest.setFrom(from);
        esRequest.setSize(size);

        FilteredQuery filteredQuery = ModelFactory.INSTANCE.filteredQuery().as();
        FilteredQuery.FilteredQueryDef filteredQueryDef = ModelFactory.INSTANCE.filteredQueryDef().as();
        filteredQuery.setFiltered(filteredQueryDef);

        QueryString queryString = ModelFactory.INSTANCE.queryString().as();
        QueryString.QueryStringDef queryStringDef = ModelFactory.INSTANCE.queryStringDef().as();
        queryString.setQueryString(queryStringDef);

        //TODO refactor this to use query filters in DTOs
        StringBuilder sb = new StringBuilder();
        fillInQuery(sb); //Move this to filter. we don't need scoring
        fillInDates(sb); //TODO move to filter
        queryStringDef.setQuery(sb.toString());

        esRequest.setSort(Collections.singletonMap("@timestamp", sortByTimestampAsc? SortOrder.asc: SortOrder.desc));

        filteredQueryDef.setQuery(queryString);
        esRequest.setQuery(filteredQuery);

        return esRequest;
    }

    public String toHistoryToken()
    {
        return Joiner.on("/").useForNull("").join(new String[]{
                String.valueOf(fromDate == null ? 0 : fromDate.getTime()),
                String.valueOf(toDate == null ? 0 : toDate.getTime()),
                String.valueOf(focusDate == null ? 0 : focusDate.getTime()),
                query
        });
    }

    public static Search fromHistoryToken(String token)
    {
        Iterator<String> parts = Splitter.on("/").limit(4).split(token).iterator();
        try
        {
            Search query = new Search();
            Long fromDate = Long.valueOf(parts.next());
            if (fromDate != 0)
            {
                query.setFromDate(new Date(fromDate));
            }
            Long toDate = Long.valueOf(parts.next());
            if (toDate != 0)
            {
                query.setToDate(new Date(toDate));
            }
            Long focusDate = Long.valueOf(parts.next());
            if (focusDate != 0)
            {
                query.setFocusDate(new Date(focusDate));
            }
            query.setQuery(parts.next());
            return query;
        }
        catch (Exception e)
        {
            return DEFAULT;
        }
    }

    private void fillInQuery(StringBuilder sb)
    {
        sb.append("(");
        if (Strings.isNullOrEmpty(query))
        {
            sb.append("\"\"");
        }
        else
        {
            sb.append(query); //TODO escape?
        }
        sb.append(")");
    }

    private void fillInDates(final StringBuilder sb)
    {
        sb.append(" AND @timestamp:[");
        if (fromDate == null)
        {
            sb.append("*");
        }
        else
        {
            sb.append(ConversionUtils.INSTANCE.dateToString(fromDate, getTimeZone()));
        }
        sb.append(" TO ");
        if (toDate == null)
        {
            sb.append("*");
        }
        else
        {
            sb.append(ConversionUtils.INSTANCE.dateToString(toDate, getTimeZone()));
        }
        sb.append("]");
    }

    private TimeZone getTimeZone()
    {
        return TimeZone.createTimeZone(0); //TODO configure and adjust. This is server timezone. Or UTC?
    }

    public String getQuery()
    {
        return query;
    }

    public Search clone()
    {
        Search clone = new Search();
        clone.query = query;
        clone.from = from;
        clone.size = size;
        clone.fromDate = fromDate;
        clone.toDate = toDate;
        clone.focusDate = focusDate;
        clone.sortByTimestampAsc = sortByTimestampAsc;

        return clone;
    }

    public void reverseSortOrder()
    {
        sortByTimestampAsc = !sortByTimestampAsc;
    }

    @Override
    public RequestBuilder.Method getMethod()
    {
        return RequestBuilder.POST;
    }
}
