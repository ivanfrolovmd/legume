package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class FocusOnDateEvent extends GwtEvent<FocusOnDateEventHandler>
{
    private final long focusDate;

    public FocusOnDateEvent(final long focusDate)
    {
        this.focusDate = focusDate;
    }

    public long getFocusDate()
    {
        return focusDate;
    }

    public static Type<FocusOnDateEventHandler> TYPE = new Type<FocusOnDateEventHandler>();

    public Type<FocusOnDateEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(FocusOnDateEventHandler handler)
    {
        handler.onFocusOnDate(this);
    }
}
