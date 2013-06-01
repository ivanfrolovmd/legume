package md.frolov.legume.client.activities.stream;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.SearchResponse;
import md.frolov.legume.client.elastic.query.SearchQuery;
import md.frolov.legume.client.events.LogMessageEvent;
import md.frolov.legume.client.events.SearchResultsReceivedEvent;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.service.ConfigurationService;

public class StreamActivity extends AbstractActivity implements StreamView.Presenter
{
    @Inject
    private StreamView streamView;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ElasticSearchService elasticSearchService;

    @Inject
    private PlaceController placeController;

    private EventBus eventBus;

    private StreamPlace place;

    private SearchQuery activeSearchQuery;
    private SearchQuery topQuery;
    private SearchQuery bottomQuery;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(streamView);
        streamView.setPresenter(this);
        place = (StreamPlace) placeController.getWhere();
        this.eventBus = eventBus;

        activeSearchQuery = place.getQuery();
        eventBus.fireEvent(new LogMessageEvent("Querying: "+place.getQuery().toQueryString()));
        eventBus.fireEvent(new UpdateSearchQuery(activeSearchQuery));
        final long started = System.currentTimeMillis();
        elasticSearchService.query(activeSearchQuery, new AsyncCallback<SearchResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                eventBus.fireEvent(new LogMessageEvent("Request failed. Network time: "+(System.currentTimeMillis()-started)/1000.0));
            }

            @Override
            public void onSuccess(final SearchResponse result)
            {
                eventBus.fireEvent(new LogMessageEvent("Search took: "+result.getTook()/1000.0+". Network time: "+(System.currentTimeMillis()-started)/1000.0));
                eventBus.fireEvent(new SearchResultsReceivedEvent(activeSearchQuery,result));
                populateDirectionalQueries(result);
            }
        }, SearchResponse.class);
    }

    private void populateDirectionalQueries(SearchResponse result)
    {
        bottomQuery = activeSearchQuery.clone();
        bottomQuery.setFromDate(Iterables.getLast(result.getHits().getHits()).getLogEvent().getTimestamp());
        bottomQuery.setToDate(null);
        bottomQuery.setFrom(result.getHits().getHits().size());

        topQuery = activeSearchQuery.clone();
        topQuery.setToDate(Iterables.getFirst(result.getHits().getHits(),null).getLogEvent().getTimestamp());
        topQuery.setFromDate(null);
        topQuery.setFrom(0);
        topQuery.reverseSortOrder();
    }

    @Override
    public void requestMoreResults(final boolean top)
    {
        final SearchQuery query = top?topQuery:bottomQuery;
        elasticSearchService.query(query, new AsyncCallback<SearchResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                eventBus.fireEvent(new LogMessageEvent("Can't fetch more results"));
            }

            @Override
            public void onSuccess(final SearchResponse result)
            {
                eventBus.fireEvent(new LogMessageEvent("Fetch more results"));
                eventBus.fireEvent(new SearchResultsReceivedEvent(query, result, top));
                query.setFrom(query.getFrom()+result.getHits().getHits().size());
            }
        }, SearchResponse.class);
    }
}
