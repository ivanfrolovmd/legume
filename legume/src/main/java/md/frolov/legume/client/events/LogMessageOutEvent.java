package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class LogMessageOutEvent extends GwtEvent<LogMessageOutEventHandler>
{
    public static Type<LogMessageOutEventHandler> TYPE = new Type<LogMessageOutEventHandler>();

    public Type<LogMessageOutEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(LogMessageOutEventHandler handler)
    {
        handler.onLogMessageOut(this);
    }
}
