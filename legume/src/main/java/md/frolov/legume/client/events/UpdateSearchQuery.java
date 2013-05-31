package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

import md.frolov.legume.client.elastic.query.SearchQuery;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class UpdateSearchQuery extends GwtEvent<UpdateSearchQueryHandler>
{
    private final SearchQuery searchQuery;

    public UpdateSearchQuery(final SearchQuery searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public SearchQuery getSearchQuery()
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
