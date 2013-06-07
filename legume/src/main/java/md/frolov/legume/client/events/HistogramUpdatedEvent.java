package md.frolov.legume.client.events;

import com.google.gwt.event.shared.GwtEvent;

import md.frolov.legume.client.elastic.model.response.SearchResponse;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class HistogramUpdatedEvent extends GwtEvent<HistogramUpdatedEventHandler>
{
    private final SearchResponse searchResponse;

    public HistogramUpdatedEvent(final SearchResponse searchResponse)
    {
        this.searchResponse = searchResponse;
    }

    public SearchResponse getSearchResponse()
    {
        return searchResponse;
    }

    public static Type<HistogramUpdatedEventHandler> TYPE = new Type<HistogramUpdatedEventHandler>();

    public Type<HistogramUpdatedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    protected void dispatch(HistogramUpdatedEventHandler handler)
    {
        handler.onHistogramUpdated(this);
    }
}
