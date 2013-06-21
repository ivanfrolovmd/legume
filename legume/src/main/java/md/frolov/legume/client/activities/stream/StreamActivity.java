package md.frolov.legume.client.activities.stream;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import md.frolov.legume.client.Application;
import md.frolov.legume.client.Constants;
import md.frolov.legume.client.activities.SearchActivity;
import md.frolov.legume.client.activities.SearchPlace;
import md.frolov.legume.client.elastic.ElasticSearchService;
import md.frolov.legume.client.elastic.api.Callback;
import md.frolov.legume.client.elastic.api.SearchRequest;
import md.frolov.legume.client.elastic.api.SearchResponse;
import md.frolov.legume.client.events.FocusOnDateEvent;
import md.frolov.legume.client.events.FocusOnDateEventHandler;
import md.frolov.legume.client.events.SearchFinishedEvent;
import md.frolov.legume.client.events.SearchInProgressEvent;
import md.frolov.legume.client.events.SearchResultsReceivedEvent;
import md.frolov.legume.client.gin.WidgetInjector;
import md.frolov.legume.client.model.Search;
import md.frolov.legume.client.service.ConfigurationService;

public class StreamActivity extends SearchActivity implements StreamView.Presenter, FocusOnDateEventHandler
{
    private static final Logger LOG = Logger.getLogger("StreamActivity");
    private static final int DEFAULT_QUERY_SIZE = WidgetInjector.INSTANCE.configurationService().getInt(Constants.PAGE_SIZE);

    @Inject
    private StreamView streamView;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ElasticSearchService elasticSearchService;

    @Inject
    private PlaceController placeController;

    @Inject
    private Application application;

    private EventBus eventBus;

    private StreamPlace place;
    private long newestDate;
    private long oldestDate;

    /** query to scroll upwards */
    private SearchRequest upwardsQuery;
    /** query to scroll downwards */
    private SearchRequest downwardsQuery;

    private boolean finished = false;

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus)
    {
        panel.setWidget(streamView);
        streamView.setPresenter(this);
        place = (StreamPlace) placeController.getWhere();
        this.eventBus = eventBus;

        eventBus.addHandler(FocusOnDateEvent.TYPE, this);

        initQueries(place.getSearch());

        requestMoreResults(false);
    }

    private void initQueries(Search search)
    {
        long focus = search.getRealFocusDate();
        downwardsQuery = new SearchRequest(new Search(search.getQuery(), focus, 0, focus), true, 0, DEFAULT_QUERY_SIZE);
        upwardsQuery = new SearchRequest(new Search(search.getQuery(), 0, focus, focus), false, 0, DEFAULT_QUERY_SIZE);
        oldestDate = focus;
        newestDate = focus;
    }

    @Override
    public void requestMoreResults(final boolean upwards)
    {
        final SearchRequest query = upwards ? upwardsQuery : downwardsQuery;
        eventBus.fireEvent(new SearchInProgressEvent(upwards));

        elasticSearchService.query(query, new Callback<SearchRequest, SearchResponse>()
        {
            @Override
            public void onFailure(final Throwable exception)
            {
                if (finished)
                {
                    return;
                }
                LOG.log(Level.SEVERE, "Can't fetch results", exception);
                eventBus.fireEvent(new SearchFinishedEvent(upwards));
            }

            @Override
            public void onSuccess(final SearchRequest query, final SearchResponse response)
            {
                if (finished)
                {
                    return;
                }
                query.setFrom(query.getFrom() + response.getHits().size());
                if(response.getHits().size()>0) {
                    long date = Iterables.getLast(response.getHits()).getLogEvent().getTimestamp().getTime();
                    if(upwards) {
                        oldestDate = date;
                    } else {
                        newestDate = date;
                    }
                }

                LOG.fine("Got reply");
                eventBus.fireEvent(new SearchResultsReceivedEvent(query, response, upwards));
                eventBus.fireEvent(new SearchFinishedEvent(upwards));
            }
        });
    }

    @Override
    public void onStop()
    {
        stop();
    }

    @Override
    public void onCancel()
    {
        stop();
    }

    private void stop()
    {
        finished = true;
        elasticSearchService.cancelAllRequests();
    }

    @Override
    public void onFocusOnDate(final FocusOnDateEvent event)
    {
        long focusDate = event.getFocusDate();
        if(focusDate>= oldestDate && focusDate<=newestDate) {
            streamView.focusOnDate(focusDate);
        }
        Search search = application.getCurrentSearch().clone();
        search.setFocusDate(focusDate);
        placeController.goTo(new StreamPlace(search));
    }

    @Override
    public boolean canActivityBeReused(final SearchPlace newPlace)
    {
        if(!(newPlace instanceof StreamPlace)) {
            return false;
        }
        return Strings.nullToEmpty(place.getSearch().getQuery()).equals(Strings.nullToEmpty(newPlace.getSearch().getQuery()))
                && newPlace.getSearch().getFocusDate() >= oldestDate
                && newPlace.getSearch().getFocusDate() <= newestDate;
    }
}
