package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

import md.frolov.legume.client.elastic.api.SearchRequest;
import md.frolov.legume.client.elastic.api.SearchResponse;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchResultsReceivedEvent extends GwtEvent<SearchResultsReceivedEventHandler>
{
    private final SearchRequest searchRequest;
    private final SearchResponse searchResponse;
    private final boolean upwards;

    public SearchResultsReceivedEvent(final SearchRequest searchRequest, final SearchResponse searchResponse, boolean upwards)
    {
        this.searchRequest = searchRequest;
        this.searchResponse = searchResponse;
        this.upwards = upwards;
    }

    public SearchRequest getSearchRequest()
    {
        return searchRequest;
    }

    public SearchResponse getSearchResponse()
    {
        return searchResponse;
    }

    public boolean isUpwards()
    {
        return upwards;
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
