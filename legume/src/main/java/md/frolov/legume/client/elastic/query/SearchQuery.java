package md.frolov.legume.client.elastic.query;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.i18n.client.TimeZone;

import md.frolov.legume.client.Constants;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.util.ConversionUtils;

public class SearchQuery implements Query
{
    public static final SearchQuery DEFAULT = new SearchQuery("", null, new Date(), null);
    private static final int DEFAULT_QUERY_SIZE = WidgetInjector.INSTANCE.configurationService().getInt(Constants.PAGE_SIZE);

    private String query;
    private List<SortOrder> sortOrders = Lists.newArrayList(SortOrder.of("@timestamp", true)); //TODO hardcoded for now. Make order configurable.
    private long from = 0;
    private int size = DEFAULT_QUERY_SIZE;
    private Date fromDate;
    private Date toDate;
    private Date focusDate;

    /** Default constructor */
    public SearchQuery()
    {

    }

    public SearchQuery(final String query, final Date fromDate, final Date toDate, final Date focusDate)
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
    public String toQueryString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("/_search?");
        fillInSortOrder(sb);

        sb.append("size=").append(size).append('&');

        if (from != 0)
        {
            sb.append("from=").append(from).append('&');
        }

        sb.append("q=");
        fillInQuery(sb);
        fillInDates(sb);
        return sb.toString();
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

    public static SearchQuery fromHistoryToken(String token)
    {
        Iterator<String> parts = Splitter.on("/").limit(4).split(token).iterator();
        try
        {
            SearchQuery query = new SearchQuery();
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

    private void fillInSortOrder(final StringBuilder sb)
    {
        if (!Iterables.isEmpty(sortOrders))
        {
            sb.append("sort=");
            sb.append(Joiner.on(',').join(sortOrders));
            sb.append('&');
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

    public SearchQuery clone()
    {
        SearchQuery clone = new SearchQuery();
        clone.query = query;
        clone.from = from;
        clone.size = size;
        clone.fromDate = fromDate;
        clone.toDate = toDate;
        clone.focusDate = focusDate;

        List<SortOrder> clonedSortOrders = Lists.newArrayList();
        for (SortOrder sortOrder : sortOrders)
        {
            clonedSortOrders.add(sortOrder.clone());
        }

        return clone;
    }

    public void reverseSortOrder()
    {
        for (SortOrder sortOrder : sortOrders)
        {
            sortOrder.setAscending(!sortOrder.isAscending());
        }
    }
}
