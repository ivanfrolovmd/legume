package md.frolov.legume.client.model;

import java.util.Date;

import com.flipthebird.gwthashcodeequals.EqualsBuilder;
import com.flipthebird.gwthashcodeequals.HashCodeBuilder;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class Search
{
    public static final Search DEFAULT = new Search("", 0, 0, 0);

    private String query;
    private long fromDate;
    private long toDate;
    private long focusDate;
    private long realFromDate;
    private long realToDate;
    private long realFocusDate;

    /**
     * Default constructor. Dates indicated here are milliseconds UTC time.
     *
     * @param query     the query
     * @param fromDate  from date. Negative values subtract from toDate. Zero means from the beginning
     * @param toDate    to date. Negative values subtract from now. Zero means up to now.
     * @param focusDate focus date. Negative values subtract form toDate. Zero means either fromDate or toDate.
     */
    public Search(final String query, final long fromDate, final long toDate, final long focusDate)
    {
        this.query = query;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.focusDate = focusDate;
        updateRealDates();
    }

    public void setQuery(final String query)
    {
        this.query = query;
    }

    public void setFromDate(final long fromDate)
    {
        this.fromDate = fromDate;
        updateRealDates();
    }

    public void setToDate(final long toDate)
    {
        this.toDate = toDate;
        updateRealDates();
    }

    public void setFocusDate(final long focusDate)
    {
        this.focusDate = focusDate;
        updateRealDates();
    }

    public String getQuery()
    {
        return query;
    }

    public long getFromDate()
    {
        return fromDate;
    }

    public long getToDate()
    {
        return toDate;
    }

    public long getFocusDate()
    {
        return focusDate;
    }

    public long getRealFromDate()
    {
        return realFromDate;
    }

    public long getRealToDate()
    {
        return realToDate;
    }

    public long getRealFocusDate()
    {
        return realFocusDate;
    }

    public Search clone()
    {
        return new Search(query, fromDate, toDate, focusDate);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(query).append(fromDate).append(toDate).append(focusDate).toHashCode();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (o instanceof Search)
        {
            Search s = (Search) o;
            return new EqualsBuilder().append(query, s.query).append(fromDate, s.fromDate).append(toDate, s.toDate)
                    .append(focusDate, s.focusDate).isEquals();
        }
        else
        {
            return false;
        }
    }

    private void updateRealDates() {
        realFromDate = fromDate;
        realToDate = toDate;
        realFocusDate = focusDate;

        long now = new Date().getTime();
        if(realToDate == 0) {
            realToDate = now;
        } else if(realToDate<0) {
            realToDate = now + realToDate;
        }
        if(realFromDate<0) {
            realFromDate = realToDate + realFromDate;
        }

        if(realFocusDate < 0) {
            realFocusDate = now + realFocusDate;
        }
        if(realFocusDate == 0 ) {
            if(toDate == 0) {
                //bind to now
                realFocusDate = now;
            }
        }
        if(realFocusDate<realFromDate){
            realFocusDate = realFromDate;
        }
        if(realFocusDate>realToDate) {
            realFocusDate = realToDate;
        }
    }
}
