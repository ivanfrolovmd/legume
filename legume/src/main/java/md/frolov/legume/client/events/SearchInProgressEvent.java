package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchInProgressEvent extends GwtEvent<SearchInProgressEventHandler>
{
    private final boolean upwards;

    public SearchInProgressEvent(final boolean upwards)
    {
        this.upwards = upwards;
    }

    public boolean isUpwards()
    {
        return upwards;
    }

    public static Type<SearchInProgressEventHandler> TYPE = new Type<SearchInProgressEventHandler>();

    public Type<SearchInProgressEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(SearchInProgressEventHandler handler)
    {
        handler.onSearchInProgress(this);
    }
}
