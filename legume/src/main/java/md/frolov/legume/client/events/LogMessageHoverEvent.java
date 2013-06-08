package md.frolov.legume.client.events;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogMessageHoverEvent extends GwtEvent<LogMessageHoverEventHandler>
{
    private final Date date;

    public LogMessageHoverEvent(final Date date)
    {
        this.date = date;
    }

    public Date getDate()
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
