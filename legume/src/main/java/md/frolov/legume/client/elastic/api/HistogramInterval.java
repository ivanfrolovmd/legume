package md.frolov.legume.client.elastic.api;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public enum HistogramInterval
{
    ms1(1,"0.001s","per 1ms", "%H:%M:%S"),
    ms10(10,"0.01s", "per 10ms", "%H:%M:%S"),
    ms100(100,"0.1s","per 100ms", "%H:%M:%S"),
    s1(1000,"1s","per 1s", "%H:%M:%S"),
    s10(10000,"10s", "per 10s", "%H:%M:%S"),
    m1(60*1000,"1m", "per 1m", "%d/%m %H:%M"),
    m10(10*60*1000,"10m","per 10m","%d/%m %H:%M"),
    h1(60*60*1000,"1h", "per 1h","%d/%m %H:%M");

    private final long time;
    private final String intervalName;
    private final String description;
    private final String dateTimeFormat;

    HistogramInterval(final long time, final String intervalName, final String description, final String dateTimeFormat)
    {
        this.time = time;
        this.intervalName = intervalName;
        this.description = description;
        this.dateTimeFormat = dateTimeFormat;
    }

    public long getTime()
    {
        return time;
    }

    public String getIntervalName()
    {
        return intervalName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDateTimeFormat()
    {
        return dateTimeFormat;
    }
}
