package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

import md.frolov.legume.client.model.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class UpdateSearchQuery extends GwtEvent<UpdateSearchQueryHandler>
{
    private final Search searchQuery;

    public UpdateSearchQuery(final Search searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public Search getSearchQuery()
    {
        return searchQuery;
    }

    public static Type<UpdateSearchQueryHandler> TYPE = new Type<UpdateSearchQueryHandler>();

    public Type<UpdateSearchQueryHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(UpdateSearchQueryHandler handler)
    {
        handler.onUpdateSearchQuery(this);
    }
}
