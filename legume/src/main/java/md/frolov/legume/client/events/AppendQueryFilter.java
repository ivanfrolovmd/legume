package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class AppendQueryFilter extends GwtEvent<AppendQueryFilterHandler>
{
    private final String filter;

    public AppendQueryFilter(final String filter)
    {
        this.filter = filter;
    }

    public String getFilter()
    {
        return filter;
    }

    public static Type<AppendQueryFilterHandler> TYPE = new Type<AppendQueryFilterHandler>();

    public Type<AppendQueryFilterHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(AppendQueryFilterHandler handler)
    {
        handler.onAppendQueryFilter(this);
    }
}
