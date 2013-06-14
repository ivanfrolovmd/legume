package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogMessageHoverEvent extends GwtEvent<LogMessageHoverEventHandler>
{
    private final long date;

    public LogMessageHoverEvent(final long date)
    {
        this.date = date;
    }

    public long getDate()
    {
        return date;
    }

    public static Type<LogMessageHoverEventHandler> TYPE = new Type<LogMessageHoverEventHandler>();

    public Type<LogMessageHoverEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(LogMessageHoverEventHandler handler)
    {
        handler.onLogMessageHover(this);
    }
}
