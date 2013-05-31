package md.frolov.legume.client.activities.stream;

import javax.inject.Inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.model.SearchResponse;
import md.frolov.legume.client.events.LogMessageEvent;
import md.frolov.legume.client.events.UpdateSearchQuery;
import md.frolov.legume.client.service.ConfigurationService;

public class StreamActivity extends AbstractActivity
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

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(streamView);
        place = (StreamPlace) placeController.getWhere();
        this.eventBus = eventBus;

        eventBus.fireEvent(new LogMessageEvent("Querying: "+place.getQuery().toQueryString()));
        eventBus.fireEvent(new UpdateSearchQuery(place.getQuery()));
        final long started = System.currentTimeMillis();
        elasticSearchService.query(place.getQuery(), new AsyncCallback<SearchResponse>()
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
                streamView.handleLogMessages(result.getHits());
            }
        }, SearchResponse.class);
    }

}
