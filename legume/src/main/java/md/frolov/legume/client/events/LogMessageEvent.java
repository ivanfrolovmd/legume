package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogMessageEvent extends GwtEvent<LogMessageEventHandler>
{
    public static Type<LogMessageEventHandler> TYPE = new Type<LogMessageEventHandler>();

    private final String message;

    public LogMessageEvent(final String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public Type<LogMessageEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(LogMessageEventHandler handler)
    {
        handler.onLogMessage(this);
    }
}
