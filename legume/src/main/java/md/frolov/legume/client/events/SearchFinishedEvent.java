package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchFinishedEvent extends GwtEvent<SearchFinishedEventHandler>
{
    private final boolean upwards;

    public SearchFinishedEvent(final boolean upwards)
    {
        this.upwards = upwards;
    }

    public boolean isUpwards()
    {
        return upwards;
    }

    public static Type<SearchFinishedEventHandler> TYPE = new Type<SearchFinishedEventHandler>();

    public Type<SearchFinishedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(SearchFinishedEventHandler handler)
    {
        handler.onSearchFinished(this);
    }
}
