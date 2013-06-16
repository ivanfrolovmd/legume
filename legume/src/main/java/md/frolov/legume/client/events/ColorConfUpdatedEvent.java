package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class ColorConfUpdatedEvent extends GwtEvent<ColorConfUpdatedEventHandler>
{
    public static Type<ColorConfUpdatedEventHandler> TYPE = new Type<ColorConfUpdatedEventHandler>();

    public Type<ColorConfUpdatedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(ColorConfUpdatedEventHandler handler)
    {
        handler.onColorConfUpdated(this);
    }
}
