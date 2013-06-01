package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

import md.frolov.legume.client.elastic.model.SearchResponse;
import md.frolov.legume.client.elastic.query.SearchQuery;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchResultsReceivedEvent extends GwtEvent<SearchResultsReceivedEventHandler>
{
    private final SearchQuery searchQuery;
    private final SearchResponse searchResponse;
    private final boolean top;

    public SearchResultsReceivedEvent(final SearchQuery searchQuery, final SearchResponse searchResponse){
        this(searchQuery, searchResponse, false);
    }

    public SearchResultsReceivedEvent(final SearchQuery searchQuery, final SearchResponse searchResponse, boolean top)
    {
        this.searchResponse = searchResponse;
        this.searchQuery = searchQuery;
        this.top = top;
    }

    public SearchResponse getSearchResponse()
    {
        return searchResponse;
    }

    public SearchQuery getSearchQuery()
    {
        return searchQuery;
    }

    public boolean isTop()
    {
        return top;
    }

    public static Type<SearchResultsReceivedEventHandler> TYPE = new Type<SearchResultsReceivedEventHandler>();

    public Type<SearchResultsReceivedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(SearchResultsReceivedEventHandler handler)
    {
        handler.onSearchResultsReceived(this);
    }
}
