package md.frolov.legume.client.elastic.query;

import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;

public class SearchQuery implements Query {
    public static final SearchQuery DEFAULT = new SearchQuery();

    private static final DateTimeFormat DTF = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private String query;
    private List<SortOrder> sortOrders = Lists.newArrayList(SortOrder.of("@timestamp",true));
    private int size = 20;
    private Date fromDate;
    private Date toDate;

    public void setQuery(final String query)
    {
        this.query = query;
    }

    public List<SortOrder> getSortOrders()
    {
        return sortOrders;
    }

    public void setSortOrders(final List<SortOrder> sortOrders)
    {
        this.sortOrders = sortOrders;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(final int size)
    {
        this.size = size;
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

    @Override
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/_search?");
        fillInSortOrder(sb);
        sb.append("size=").append(size).append('&');

        sb.append("q=");
        fillInQuery(sb);
        fillInDates(sb);
        return sb.toString();
    }

    private void fillInSortOrder(final StringBuilder sb)
    {
        if(!Iterables.isEmpty(sortOrders)) {
            sb.append("sort=");
            sb.append(Joiner.on(',').join(sortOrders));
            sb.append('&');
        }
    }

    private void fillInQuery(StringBuilder sb) {
        sb.append("(");
        if(Strings.isNullOrEmpty(query)){
            sb.append("\"\"");
        } else {
            sb.append(query); //TODO escape?
        }
        sb.append(")");
    }

    private void fillInDates(final StringBuilder sb)
    {
        sb.append("AND @timestamp:[");
        if(fromDate==null) {
            sb.append("*");
        } else {
            sb.append(DTF.format(fromDate, getTimeZone()));
        }
        sb.append(" TO ");
        if(toDate==null) {
            sb.append("*");
        } else {
            sb.append(DTF.format(toDate, getTimeZone()));
        }
        sb.append("]");
    }

    private TimeZone getTimeZone() {
        return TimeZone.createTimeZone(0); //TODO configure and adjust
    }

    public String getQuery()
    {
        return query;
    }
}
