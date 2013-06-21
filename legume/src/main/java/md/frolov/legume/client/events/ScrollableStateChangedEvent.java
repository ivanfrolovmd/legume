package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ScrollableStateChangedEvent extends GwtEvent<ScrollableStateChangedEventHandler>
{
    private final boolean isScrollable;

    public ScrollableStateChangedEvent(final boolean scrollable)
    {
        isScrollable = scrollable;
    }

    public boolean isScrollable()
    {
        return isScrollable;
    }

    public static Type<ScrollableStateChangedEventHandler> TYPE = new Type<ScrollableStateChangedEventHandler>();

    public Type<ScrollableStateChangedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(ScrollableStateChangedEventHandler handler)
    {
        handler.onScrollableStateChanged(this);
    }
}
