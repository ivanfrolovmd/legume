package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

import md.frolov.legume.client.elastic.model.reply.ElasticSearchReply;
import md.frolov.legume.client.elastic.query.Search;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class SearchResultsReceivedEvent extends GwtEvent<SearchResultsReceivedEventHandler>
{
    private final Search searchQuery;
    private final ElasticSearchReply searchResponse;
    private final boolean top;

    public SearchResultsReceivedEvent(final Search searchQuery, final ElasticSearchReply elasticSearchReply){
        this(searchQuery, elasticSearchReply, false);
    }

    public SearchResultsReceivedEvent(final Search searchQuery, final ElasticSearchReply elasticSearchReply, boolean top)
    {
        this.searchResponse = elasticSearchReply;
        this.searchQuery = searchQuery;
        this.top = top;
    }

    public ElasticSearchReply getSearchResponse()
    {
        return searchResponse;
    }

    public Search getSearchQuery()
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
